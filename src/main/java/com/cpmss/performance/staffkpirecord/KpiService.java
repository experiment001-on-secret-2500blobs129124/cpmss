package com.cpmss.performance.staffkpirecord;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.performance.kpipolicy.KpiPolicy;
import com.cpmss.performance.kpipolicy.KpiPolicyRepository;
import com.cpmss.performance.common.PerformanceAccessRules;
import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.performance.staffkpimonthlysummary.StaffKpiMonthlySummary;
import com.cpmss.performance.staffkpimonthlysummary.StaffKpiMonthlySummaryRepository;
import com.cpmss.performance.staffkpimonthlysummary.StaffKpiMonthlySummaryRules;
import com.cpmss.performance.staffkpimonthlysummary.dto.StaffKpiMonthlySummaryResponse;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.staffkpirecord.dto.CreateStaffKpiRecordRequest;
import com.cpmss.performance.staffkpirecord.dto.StaffKpiRecordResponse;
import com.cpmss.platform.common.value.YearMonthPeriod;
import com.cpmss.platform.exception.ApiException;
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
 * <p>Request and response DTOs keep primitive score and period fields, while
 * this service converts score input and monthly snapshot output through
 * {@link KpiScore}. Month values are validated with
 * {@link YearMonthPeriod}.
 *
 * @see KpiScore
 * @see YearMonthPeriod
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
    private final CurrentUserService currentUserService;
    private final DepartmentScopeService departmentScopeService;
    private final StaffKpiRecordRules recordRules = new StaffKpiRecordRules();
    private final PerformanceAccessRules accessRules = new PerformanceAccessRules();
    private final StaffKpiMonthlySummaryRules summaryRules = new StaffKpiMonthlySummaryRules();

    /**
     * Creates the KPI service.
     *
     * @param kpiRecordRepository repository for daily KPI records
     * @param kpiSummaryRepository repository for monthly KPI summaries
     * @param personRepository repository used to resolve staff, managers, and
     *                         closers
     * @param departmentRepository repository used to resolve departments
     * @param kpiPolicyRepository repository used to resolve KPI policy tiers
     */
    public KpiService(StaffKpiRecordRepository kpiRecordRepository,
                      StaffKpiMonthlySummaryRepository kpiSummaryRepository,
                      PersonRepository personRepository,
                      DepartmentRepository departmentRepository,
                      KpiPolicyRepository kpiPolicyRepository,
                      CurrentUserService currentUserService,
                      DepartmentScopeService departmentScopeService) {
        this.kpiRecordRepository = kpiRecordRepository;
        this.kpiSummaryRepository = kpiSummaryRepository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.kpiPolicyRepository = kpiPolicyRepository;
        this.currentUserService = currentUserService;
        this.departmentScopeService = departmentScopeService;
    }

    // ── Daily KPI Recording ─────────────────────────────────────────────

    /**
     * Records a daily KPI score for a staff member.
     *
     * @param request the KPI record details
     * @return the created KPI record response
     * @throws ApiException if the referenced staff, department, policy, or
     *                      recorder does not exist, or if the score or policy
     *                      state is invalid
     */
    @Transactional
    public StaffKpiRecordResponse recordDailyKpi(CreateStaffKpiRecordRequest request) {
        CurrentUser user = currentUserService.currentUser();
        accessRules.requireCanManageDepartment(
                user, request.departmentId(), departmentScopeService);
        if (!accessRules.isHrOrBusinessAdmin(user)
                && (!request.recordedById().equals(user.personId())
                || !departmentScopeService.staffBelongsToDepartment(
                        request.staffId(), request.departmentId()))) {
            throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
        }
        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        KpiPolicy policy = kpiPolicyRepository.findById(request.kpiPolicyId())
                .orElseThrow(() -> new ApiException(PerformanceErrorCode.KPI_POLICY_NOT_FOUND));
        Person recordedBy = personRepository.findById(request.recordedById())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        recordRules.validatePolicyActiveForDate(policy, request.recordDate());
        KpiScore score = KpiScore.of(request.kpiScore());

        StaffKpiRecord record = new StaffKpiRecord();
        record.setStaff(staff);
        record.setDepartment(department);
        record.setRecordDate(request.recordDate());
        record.setKpiScore(score);
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
        CurrentUser user = currentUserService.currentUser();
        YearMonthPeriod period = YearMonthPeriod.of(year, month);
        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();
        return kpiRecordRepository.findByStaffIdAndRecordDateBetween(staffId, from, to)
                .stream()
                .filter(record -> canViewKpiRecord(user, record))
                .map(this::toKpiRecordResponse)
                .toList();
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
          * @throws ApiException if the department or closer does not exist, or the
          *                      closer/period is invalid
     */
    @Transactional
    public List<StaffKpiMonthlySummaryResponse> closeMonthlyKpi(
            UUID departmentId, int year, int month, UUID closedById) {

        CurrentUser user = currentUserService.currentUser();
        summaryRules.validateCloserProvided(closedById != null);
        accessRules.requireCanManageDepartment(user, departmentId, departmentScopeService);
        if (!accessRules.isHrOrBusinessAdmin(user) && !closedById.equals(user.personId())) {
            throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
        }
        YearMonthPeriod period = YearMonthPeriod.of(year, month);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        Person closedBy = personRepository.findById(closedById)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));

        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();

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
            summary.setYear(period.year());
            summary.setMonth(period.month());
            summary.setAvgKpiScore(KpiScore.of(avgScore));
            summary.setTotalKpiScore(KpiScore.of(totalScore));
            summary.setDaysScored(daysScored);
            summary.setApplicableTier(policy.getTierLabelValue());
            summary.setPayrollBonusRate(policy.getBonusRateValue());
            summary.setPayrollDeductRate(policy.getDeductionRateValue());
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
     *
     * @param departmentId the department UUID
     * @param year the calendar year
     * @param month the calendar month number from 1 to 12
     * @return KPI summary responses for the department and period
     */
    @Transactional(readOnly = true)
    public List<StaffKpiMonthlySummaryResponse> getKpiSummaries(
            UUID departmentId, int year, int month) {
        accessRules.requireCanManageDepartment(
                currentUserService.currentUser(), departmentId, departmentScopeService);
        return kpiSummaryRepository.findByDepartmentIdAndYearAndMonth(departmentId, year, month)
                .stream().map(this::toSummaryResponse).toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private boolean canViewKpiRecord(CurrentUser user, StaffKpiRecord record) {
        try {
            accessRules.requireCanViewStaffPerformance(
                    user, record.getStaff().getId(),
                    record.getDepartment().getId(), departmentScopeService);
            return true;
        } catch (ApiException ex) {
            return false;
        }
    }

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
