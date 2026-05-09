package com.cpmss.workforce.attends;

import com.cpmss.finance.money.Money;
import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.workforce.attends.dto.AttendsResponse;
import com.cpmss.workforce.attends.dto.CreateAttendsRequest;
import com.cpmss.workforce.assignedtask.AssignedTaskRepository;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.workforce.common.WorkforceAccessRules;
import com.cpmss.workforce.common.WorkforceErrorCode;
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
import java.util.function.Function;

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
    private final CurrentUserService currentUserService;
    private final DepartmentScopeService departmentScopeService;
    private final AttendsRules attendsRules = new AttendsRules();
    private final StaffSalaryRules salaryRules = new StaffSalaryRules();
    private final WorkforceAccessRules workforceAccessRules = new WorkforceAccessRules();
    private final HrAccessRules hrAccessRules = new HrAccessRules();

    /**
     * Constructs the payroll service with repositories used by attendance,
     * payroll rollup, and salary history flows.
     *
     * @param attendsRepository attendance data access
     * @param monthlySalaryRepository monthly payroll snapshot data access
     * @param salaryHistoryRepository staff salary history data access
     * @param personRepository person data access
     * @param shiftRepository shift attendance type data access
     * @param departmentRepository department data access
     * @param assignedTaskRepository assigned-task data access
     */
    public PayrollService(AttendsRepository attendsRepository,
                          TaskMonthlySalaryRepository monthlySalaryRepository,
                          StaffSalaryHistoryRepository salaryHistoryRepository,
                          PersonRepository personRepository,
                          ShiftAttendanceTypeRepository shiftRepository,
                          DepartmentRepository departmentRepository,
                          AssignedTaskRepository assignedTaskRepository,
                          CurrentUserService currentUserService,
                          DepartmentScopeService departmentScopeService) {
        this.attendsRepository = attendsRepository;
        this.monthlySalaryRepository = monthlySalaryRepository;
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.personRepository = personRepository;
        this.shiftRepository = shiftRepository;
        this.departmentRepository = departmentRepository;
        this.assignedTaskRepository = assignedTaskRepository;
        this.currentUserService = currentUserService;
        this.departmentScopeService = departmentScopeService;
    }

    // ── Attendance Operations ───────────────────────────────────────────

    /**
     * Records daily attendance for a staff member.
     *
     * @param request the attendance details
     * @return the created attendance response
     * @throws ApiException if staff or shift not found
     */
    @Transactional
    public AttendsResponse recordAttendance(CreateAttendsRequest request) {
        CurrentUser user = currentUserService.currentUser();
        UUID departmentId = departmentScopeService.activeDepartmentForStaff(request.staffId())
                .orElse(null);
        workforceAccessRules.requireCanManageDepartment(
                user, departmentId, departmentScopeService);

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        ShiftAttendanceType shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));

        // Validate assignment exists
        boolean hasAssignment = assignedTaskRepository.existsByStaffIdAndAssignmentDate(
                request.staffId(), request.date());
        attendsRules.validateHasAssignedTask(hasAssignment);

        // Validate no duplicate
        AttendsId id = new AttendsId(request.staffId(), request.shiftId(), request.date());
        attendsRules.validateNoDuplicate(attendsRepository.existsById(id));

        // Validate times when present
        attendsRules.validateTimesWhenPresent(request.isAbsent(), request.attendanceWindow());

        Attends attends = new Attends();
        attends.setStaff(staff);
        attends.setShift(shift);
        attends.setDate(request.date());
        attends.setIsAbsent(request.isAbsent());
        attends.setAttendanceWindow(request.attendanceWindow());
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
        CurrentUser user = currentUserService.currentUser();
        YearMonthPeriod period = YearMonthPeriod.of(year, month);
        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();
        return attendsRepository.findByStaffIdAndDateBetween(staffId, from, to)
                .stream()
                .filter(attends -> canViewAttendance(user, attends))
                .map(this::toAttendsResponse)
                .toList();
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
     * @param currency     ISO-4217 currency for zero or newly created snapshots
     * @return list of created monthly salary responses
     */
    @Transactional
    public List<TaskMonthlySalaryResponse> closeMonthlyPayroll(
            UUID departmentId, int year, int month, String currency) {
        workforceAccessRules.requirePayrollFinance(currentUserService.currentUser());
        YearMonthPeriod period = YearMonthPeriod.of(year, month);
        Money zero = new Money(BigDecimal.ZERO, currency);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        LocalDate from = period.firstDay();
        LocalDate to = period.lastDay();

        // Get all attendance in the period — group by staff
        // For simplicity, get all and aggregate in memory
        List<Attends> allAttendance = attendsRepository.findAll().stream()
                .filter(a -> !a.getDate().isBefore(from) && !a.getDate().isAfter(to))
                .filter(a -> departmentScopeService.staffBelongsToDepartment(
                        a.getStaff().getId(), departmentId))
                .toList();

        // Group by staff+shift
        var grouped = allAttendance.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> a.getStaff().getId() + "|" + a.getShift().getId()));

        List<TaskMonthlySalary> results = new java.util.ArrayList<>();
        for (var entry : grouped.entrySet()) {
            List<Attends> records = entry.getValue();
            Attends sample = records.get(0);

            Money totalSalary = sumMoney(records, Attends::getDailySalary, zero);
            Money totalBonus = sumMoney(records, Attends::getDailyBonus, zero);
            Money totalDeduction = sumMoney(records, Attends::getDailyDeduction, zero);
            BigDecimal taxAmount = totalSalary.getAmount().multiply(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);
            Money tax = new Money(taxAmount, totalSalary.getCurrency());
            BigDecimal netSalaryAmount = totalSalary.getAmount()
                    .add(totalBonus.getAmount())
                    .subtract(totalDeduction.getAmount())
                    .subtract(tax.getAmount());
            Money netSalary = new Money(netSalaryAmount, totalSalary.getCurrency());

            TaskMonthlySalary monthly = new TaskMonthlySalary();
            monthly.setStaff(sample.getStaff());
            monthly.setDepartment(department);
            monthly.setShift(sample.getShift());
            monthly.setPayrollPeriod(period);
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
        workforceAccessRules.requirePayrollFinance(currentUserService.currentUser());
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
        hrAccessRules.requireHrAdministrator(currentUserService.currentUser());
        salaryRules.validateBaseDailyRatePositive(request.baseDailyRate());
        salaryRules.validateMaximumSalaryPositive(request.maximumSalary());

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Person approver = request.approvedById() != null
                ? personRepository.findById(request.approvedById())
                        .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND))
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

    private boolean canViewAttendance(CurrentUser user, Attends attends) {
        UUID departmentId = departmentScopeService.activeDepartmentForStaff(
                attends.getStaff().getId()).orElse(null);
        try {
            workforceAccessRules.requireCanViewStaffWorkforce(
                    user, attends.getStaff().getId(), departmentId, departmentScopeService);
            return true;
        } catch (ApiException ex) {
            return false;
        }
    }

    private AttendsResponse toAttendsResponse(Attends a) {
        return new AttendsResponse(
                a.getStaff().getId(), a.getShift().getId(), a.getDate(),
                a.getIsAbsent(), a.getAttendanceWindow(),
                a.getPeriodOutIn(), a.getDiffHour(),
                a.getDailyBonus(), a.getDailyDeduction(),
                a.getDailySalary(), a.getDailyNetSalary());
    }

    private TaskMonthlySalaryResponse toMonthlySalaryResponse(TaskMonthlySalary m) {
        return new TaskMonthlySalaryResponse(
                m.getStaff().getId(), m.getDepartment().getId(), m.getShift().getId(),
                m.getPayrollPeriod(),
                m.getMonthlyDeduction(), m.getMonthlyBonus(), m.getTax(),
                m.getMonthlySalary(), m.getMonthlyNetSalary());
    }

    private Money sumMoney(List<Attends> records, Function<Attends, Money> extractor, Money zero) {
        return records.stream()
                .map(extractor)
                .filter(java.util.Objects::nonNull)
                .reduce(zero, Money::add);
    }

    private StaffSalaryHistoryResponse toSalaryHistoryResponse(StaffSalaryHistory h) {
        return new StaffSalaryHistoryResponse(
                h.getStaff().getId(), h.getEffectiveDate(), h.getEndDate(),
                h.getBaseDailyRate(), h.getMaximumSalary(),
                h.getApprovedBy() != null ? h.getApprovedBy().getId() : null,
                h.getReviewId());
    }
}
