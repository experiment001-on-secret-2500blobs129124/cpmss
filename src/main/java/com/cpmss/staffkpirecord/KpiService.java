package com.cpmss.staffkpirecord;

import com.cpmss.department.Department;
import com.cpmss.department.DepartmentRepository;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.kpipolicy.KpiPolicy;
import com.cpmss.kpipolicy.KpiPolicyRepository;
import com.cpmss.person.Person;
import com.cpmss.person.PersonRepository;
import com.cpmss.staffkpimonthlysummary.StaffKpiMonthlySummary;
import com.cpmss.staffkpimonthlysummary.StaffKpiMonthlySummaryRepository;
import com.cpmss.staffkpimonthlysummary.StaffKpiMonthlySummaryRules;
import com.cpmss.staffkpimonthlysummary.dto.StaffKpiMonthlySummaryResponse;
import com.cpmss.staffkpirecord.dto.CreateStaffKpiRecordRequest;
import com.cpmss.staffkpirecord.dto.StaffKpiRecordResponse;
import com.cpmss.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Orchestrates KPI scoring and monthly close workflow (US-9).
 *
 * @see StaffKpiRecordRules
 * @see StaffKpiMonthlySummaryRules
 */
@Service
public class KpiService {

    private static final Logger log = LoggerFactory.getLogger(KpiService.class);

    private final StaffKpiRecordRepository kpiRecordRepository;
    private final StaffKpiMonthlySummaryRepository kpiSummaryRepository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final KpiPolicyRepository kpiPolicyRepository;
    private final StaffKpiRecordRules recordRules = new StaffKpiRecordRules();
    private final StaffKpiMonthlySummaryRules summaryRules = new StaffKpiMonthlySummaryRules();

    public KpiService(StaffKpiRecordRepository kpiRecordRepository,
                      StaffKpiMonthlySummaryRepository kpiSummaryRepository,
                      PersonRepository personRepository,
                      DepartmentRepository departmentRepository,
                      KpiPolicyRepository kpiPolicyRepository) {
        this.kpiRecordRepository = kpiRecordRepository;
        this.kpiSummaryRepository = kpiSummaryRepository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.kpiPolicyRepository = kpiPolicyRepository;
    }

    // ── Daily KPI Recording ─────────────────────────────────────────────

    /**
     * Records a daily KPI score for a staff member.
     *
     * @param request the KPI record details
     * @return the created KPI record response
     */
    @Transactional
    public StaffKpiRecordResponse recordDailyKpi(CreateStaffKpiRecordRequest request) {
        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));
        KpiPolicy policy = kpiPolicyRepository.findById(request.kpiPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("KpiPolicy", request.kpiPolicyId()));
        Person recordedBy = personRepository.findById(request.recordedById())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.recordedById()));

        recordRules.validatePolicyActiveForDate(policy, request.recordDate());

        StaffKpiRecord record = new StaffKpiRecord();
        record.setStaff(staff);
        record.setDepartment(department);
        record.setRecordDate(request.recordDate());
        record.setKpiScore(request.kpiScore());
        record.setKpiPolicy(policy);
        record.setRecordedBy(recordedBy);
        record.setNotes(request.notes());
        record = kpiRecordRepository.save(record);

        log.info("KPI recorded: staff={}, department={}, date={}, score={}",
                request.staffId(), request.departmentId(),
                request.recordDate(), request.kpiScore());
        return toKpiRecordResponse(record);
    }

    /**
     * Retrieves KPI records for a staff member in a given month.
     *
     * @param staffId the staff member's person UUID
     * @param year    the year
     * @param month   the month (1-12)
     * @return list of KPI record responses
     */
    @Transactional(readOnly = true)
    public List<StaffKpiRecordResponse> getKpiByStaff(UUID staffId, int year, int month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        return kpiRecordRepository.findByStaffIdAndRecordDateBetween(staffId, from, to)
                .stream().map(this::toKpiRecordResponse).toList();
    }

    // ── Monthly KPI Close ───────────────────────────────────────────────

    /**
     * Closes monthly KPI for a department — aggregates daily records into summaries.
     *
     * @param departmentId the department UUID
     * @param year         the year
     * @param month        the month (1-12)
     * @param closedById   the manager/HR who is closing
     * @return list of created summary responses
     */
    @Transactional
    public List<StaffKpiMonthlySummaryResponse> closeMonthlyKpi(
            UUID departmentId, int year, int month, UUID closedById) {

        summaryRules.validateCloserProvided(closedById != null);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
        Person closedBy = personRepository.findById(closedById)
                .orElseThrow(() -> new ResourceNotFoundException("Person", closedById));

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<StaffKpiRecord> records = kpiRecordRepository
                .findByDepartmentIdAndRecordDateBetween(departmentId, from, to);

        // Group by staff
        Map<UUID, List<StaffKpiRecord>> byStaff = records.stream()
                .collect(Collectors.groupingBy(r -> r.getStaff().getId()));

        List<StaffKpiMonthlySummary> results = new java.util.ArrayList<>();
        for (var entry : byStaff.entrySet()) {
            List<StaffKpiRecord> staffRecords = entry.getValue();
            StaffKpiRecord sample = staffRecords.get(0);

            BigDecimal totalScore = staffRecords.stream()
                    .map(StaffKpiRecord::getKpiScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int daysScored = staffRecords.size();
            BigDecimal avgScore = totalScore.divide(
                    BigDecimal.valueOf(daysScored), 2, RoundingMode.HALF_UP);

            // Use the last record's policy for tier info
            KpiPolicy policy = sample.getKpiPolicy();

            StaffKpiMonthlySummary summary = new StaffKpiMonthlySummary();
            summary.setStaff(sample.getStaff());
            summary.setDepartment(department);
            summary.setYear(year);
            summary.setMonth(month);
            summary.setAvgKpiScore(avgScore);
            summary.setTotalKpiScore(totalScore);
            summary.setDaysScored(daysScored);
            summary.setApplicableTier(policy.getTierLabel());
            summary.setPayrollBonusRate(policy.getBonusRate());
            summary.setPayrollDeductRate(policy.getDeductionRate());
            summary.setKpiPolicy(policy);
            summary.setClosedBy(closedBy);
            results.add(kpiSummaryRepository.save(summary));
        }

        log.info("Monthly KPI closed: department={}, period={}-{}, summaries={}",
                departmentId, year, month, results.size());
        return results.stream().map(this::toSummaryResponse).toList();
    }

    /**
     * Retrieves KPI summaries for a department in a given period.
     */
    @Transactional(readOnly = true)
    public List<StaffKpiMonthlySummaryResponse> getKpiSummaries(
            UUID departmentId, int year, int month) {
        return kpiSummaryRepository.findByDepartmentIdAndYearAndMonth(departmentId, year, month)
                .stream().map(this::toSummaryResponse).toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private StaffKpiRecordResponse toKpiRecordResponse(StaffKpiRecord r) {
        return new StaffKpiRecordResponse(
                r.getStaff().getId(), r.getDepartment().getId(),
                r.getRecordDate(), r.getKpiScore(),
                r.getKpiPolicy().getId(), r.getRecordedBy().getId(),
                r.getNotes());
    }

    private StaffKpiMonthlySummaryResponse toSummaryResponse(StaffKpiMonthlySummary s) {
        return new StaffKpiMonthlySummaryResponse(
                s.getStaff().getId(), s.getDepartment().getId(),
                s.getYear(), s.getMonth(),
                s.getAvgKpiScore(), s.getTotalKpiScore(), s.getDaysScored(),
                s.getApplicableTier(), s.getPayrollBonusRate(), s.getPayrollDeductRate(),
                s.getKpiPolicy() != null ? s.getKpiPolicy().getId() : null,
                s.getClosedBy().getId());
    }
}
