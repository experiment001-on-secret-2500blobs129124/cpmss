package com.cpmss.workforce.attends;

import com.cpmss.workforce.attends.dto.AttendsResponse;
import com.cpmss.workforce.attends.dto.CreateAttendsRequest;
import com.cpmss.workforce.assignedtask.AssignedTaskRepository;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceTypeRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistory;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryRules;
import com.cpmss.hr.staffsalaryhistory.dto.CreateStaffSalaryHistoryRequest;
import com.cpmss.hr.staffsalaryhistory.dto.StaffSalaryHistoryResponse;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalary;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalaryRepository;
import com.cpmss.workforce.taskmonthlysalary.dto.TaskMonthlySalaryResponse;
import com.cpmss.platform.common.value.YearMonthPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates attendance recording, monthly payroll close, and salary changes.
 *
 * <p>Manages the daily attendance → monthly aggregation → payroll snapshot
 * lifecycle described in REQUIREMENTS.md US-8.
 *
 * @see AttendsRules
 * @see StaffSalaryRules
 */
@Service
public class PayrollService {

    private static final Logger log = LoggerFactory.getLogger(PayrollService.class);

    private final AttendsRepository attendsRepository;
    private final TaskMonthlySalaryRepository monthlySalaryRepository;
    private final StaffSalaryHistoryRepository salaryHistoryRepository;
    private final PersonRepository personRepository;
    private final ShiftAttendanceTypeRepository shiftRepository;
    private final DepartmentRepository departmentRepository;
    private final AssignedTaskRepository assignedTaskRepository;
    private final AttendsRules attendsRules = new AttendsRules();
    private final StaffSalaryRules salaryRules = new StaffSalaryRules();

    public PayrollService(AttendsRepository attendsRepository,
                          TaskMonthlySalaryRepository monthlySalaryRepository,
                          StaffSalaryHistoryRepository salaryHistoryRepository,
                          PersonRepository personRepository,
                          ShiftAttendanceTypeRepository shiftRepository,
                          DepartmentRepository departmentRepository,
                          AssignedTaskRepository assignedTaskRepository) {
        this.attendsRepository = attendsRepository;
        this.monthlySalaryRepository = monthlySalaryRepository;
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.personRepository = personRepository;
        this.shiftRepository = shiftRepository;
        this.departmentRepository = departmentRepository;
        this.assignedTaskRepository = assignedTaskRepository;
    }

    // ── Attendance Operations ───────────────────────────────────────────

    /**
     * Records daily attendance for a staff member.
     *
     * @param request the attendance details
     * @return the created attendance response
     * @throws ResourceNotFoundException if staff or shift not found
     */
    @Transactional
    public AttendsResponse recordAttendance(CreateAttendsRequest request) {
        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        ShiftAttendanceType shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("ShiftAttendanceType", request.shiftId()));

        // Validate assignment exists
        boolean hasAssignment = assignedTaskRepository.existsByStaffIdAndAssignmentDate(
                request.staffId(), request.date());
        attendsRules.validateHasAssignedTask(hasAssignment);

        // Validate no duplicate
        AttendsId id = new AttendsId(request.staffId(), request.shiftId(), request.date());
        attendsRules.validateNoDuplicate(attendsRepository.existsById(id));

        // Validate times when present
        attendsRules.validateTimesWhenPresent(
                request.isAbsent(),
                request.checkInTime() != null,
                request.checkOutTime() != null);

        Attends attends = new Attends();
        attends.setStaff(staff);
        attends.setShift(shift);
        attends.setDate(request.date());
        attends.setIsAbsent(request.isAbsent());
        attends.setCheckInTime(request.checkInTime());
        attends.setCheckOutTime(request.checkOutTime());
        attends.setPeriodOutIn(request.periodOutIn());
        attends.setDiffHour(request.diffHour());
        attends = attendsRepository.save(attends);
        log.info("Attendance recorded: staff={}, shift={}, date={}",
                request.staffId(), request.shiftId(), request.date());
        return toAttendsResponse(attends);
    }

    /**
     * Retrieves attendance records for a staff member in a given month.
     *
     * @param staffId the staff member's person UUID
     * @param year    the year
     * @param month   the month (1-12)
     * @return list of attendance responses
     */
    @Transactional(readOnly = true)
    public List<AttendsResponse> getAttendanceByStaff(UUID staffId, int year, int month) {
        YearMonthPeriod period = YearMonthPeriod.of(year, month);
        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();
        return attendsRepository.findByStaffIdAndDateBetween(staffId, from, to)
                .stream().map(this::toAttendsResponse).toList();
    }

    // ── Monthly Payroll Close ───────────────────────────────────────────

    /**
     * Closes monthly payroll for a department — aggregates daily attendance into
     * TaskMonthlySalary snapshots.
     *
     * <p>For each staff member with attendance in the period, sums daily
     * salary/bonus/deduction fields into a monthly record. Once closed,
     * these values are frozen.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month (1-12)
     * @return list of created monthly salary responses
     */
    @Transactional
    public List<TaskMonthlySalaryResponse> closeMonthlyPayroll(UUID departmentId, int year, int month) {
        YearMonthPeriod period = YearMonthPeriod.of(year, month);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();

        // Get all attendance in the period — group by staff
        // For simplicity, get all and aggregate in memory
        List<Attends> allAttendance = attendsRepository.findAll().stream()
                .filter(a -> !a.getDate().isBefore(from) && !a.getDate().isAfter(to))
                .toList();

        // Group by staff+shift
        var grouped = allAttendance.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> a.getStaff().getId() + "|" + a.getShift().getId()));

        List<TaskMonthlySalary> results = new java.util.ArrayList<>();
        for (var entry : grouped.entrySet()) {
            List<Attends> records = entry.getValue();
            Attends sample = records.get(0);

            BigDecimal totalSalary = records.stream()
                    .map(a -> a.getDailySalary() != null ? a.getDailySalary() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalBonus = records.stream()
                    .map(a -> a.getDailyBonus() != null ? a.getDailyBonus() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalDeduction = records.stream()
                    .map(a -> a.getDailyDeduction() != null ? a.getDailyDeduction() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tax = totalSalary.multiply(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal netSalary = totalSalary.add(totalBonus)
                    .subtract(totalDeduction).subtract(tax);

            TaskMonthlySalary monthly = new TaskMonthlySalary();
            monthly.setStaff(sample.getStaff());
            monthly.setDepartment(department);
            monthly.setShift(sample.getShift());
            monthly.setYear(period.year());
            monthly.setMonth(period.month());
            monthly.setMonthlySalary(totalSalary);
            monthly.setMonthlyBonus(totalBonus);
            monthly.setMonthlyDeduction(totalDeduction);
            monthly.setTax(tax);
            monthly.setMonthlyNetSalary(netSalary);
            results.add(monthlySalaryRepository.save(monthly));
        }

        log.info("Monthly payroll closed: department={}, period={}-{}, records={}",
                departmentId, year, month, results.size());
        return results.stream().map(this::toMonthlySalaryResponse).toList();
    }

    /**
     * Retrieves monthly payroll records for a department in a given period.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month
     * @return list of monthly salary responses
     */
    @Transactional(readOnly = true)
    public List<TaskMonthlySalaryResponse> getMonthlyPayroll(UUID departmentId, int year, int month) {
        return monthlySalaryRepository.findByDepartmentIdAndYearAndMonth(departmentId, year, month)
                .stream().map(this::toMonthlySalaryResponse).toList();
    }

    // ── Salary History ──────────────────────────────────────────────────

    /**
     * Creates a new salary history record (raise/change).
     *
     * <p>Closes the currently active rate (sets end_date) and opens
     * a new row with the new rate. SCD Type 2 pattern.
     *
     * @param request the salary change details
     * @return the created salary history response
     */
    @Transactional
    public StaffSalaryHistoryResponse createSalaryChange(CreateStaffSalaryHistoryRequest request) {
        salaryRules.validateBaseDailyRatePositive(request.baseDailyRate());
        salaryRules.validateMaximumSalaryPositive(request.maximumSalary());

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Person approver = request.approvedById() != null
                ? personRepository.findById(request.approvedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Person", request.approvedById()))
                : null;

        // Close current active rate (end_date IS NULL)
        salaryHistoryRepository.findAll().stream()
                .filter(h -> h.getStaff().getId().equals(request.staffId()) && h.getEndDate() == null)
                .findFirst()
                .ifPresent(current -> {
                    current.setEndDate(request.effectiveDate().minusDays(1));
                    salaryHistoryRepository.save(current);
                });

        // Create new rate
        StaffSalaryHistory newRate = new StaffSalaryHistory();
        newRate.setStaff(staff);
        newRate.setEffectiveDate(request.effectiveDate());
        newRate.setBaseDailyRate(request.baseDailyRate());
        newRate.setMaximumSalary(request.maximumSalary());
        newRate.setApprovedBy(approver);
        newRate.setReviewId(request.reviewId());
        newRate = salaryHistoryRepository.save(newRate);

        log.info("Salary change recorded: staff={}, newRate={}, effective={}",
                request.staffId(), request.baseDailyRate(), request.effectiveDate());
        return toSalaryHistoryResponse(newRate);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private AttendsResponse toAttendsResponse(Attends a) {
        return new AttendsResponse(
                a.getStaff().getId(), a.getShift().getId(), a.getDate(),
                a.getIsAbsent(), a.getCheckInTime(), a.getCheckOutTime(),
                a.getPeriodOutIn(), a.getDiffHour(),
                a.getDailyBonus(), a.getDailyDeduction(),
                a.getDailySalary(), a.getDailyNetSalary());
    }

    private TaskMonthlySalaryResponse toMonthlySalaryResponse(TaskMonthlySalary m) {
        return new TaskMonthlySalaryResponse(
                m.getStaff().getId(), m.getDepartment().getId(), m.getShift().getId(),
                m.getYear(), m.getMonth(),
                m.getMonthlyDeduction(), m.getMonthlyBonus(), m.getTax(),
                m.getMonthlySalary(), m.getMonthlyNetSalary());
    }

    private StaffSalaryHistoryResponse toSalaryHistoryResponse(StaffSalaryHistory h) {
        return new StaffSalaryHistoryResponse(
                h.getStaff().getId(), h.getEffectiveDate(), h.getEndDate(),
                h.getBaseDailyRate(), h.getMaximumSalary(),
                h.getApprovedBy() != null ? h.getApprovedBy().getId() : null,
                h.getReviewId());
    }
}
