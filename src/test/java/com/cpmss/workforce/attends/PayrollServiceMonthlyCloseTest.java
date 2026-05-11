package com.cpmss.workforce.attends;

import com.cpmss.finance.money.Money;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendance;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendanceRepository;
import com.cpmss.hr.staffposition.PositionSalaryHistoryRepository;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistory;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.assignedtask.AssignedTaskRepository;
import com.cpmss.workforce.common.HourDelta;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceTypeRepository;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalary;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalaryRepository;
import com.cpmss.workforce.taskmonthlysalary.dto.TaskMonthlySalaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies payroll close computes and freezes daily attendance snapshots before
 * monthly payroll rows are created.
 */
@ExtendWith(MockitoExtension.class)
class PayrollServiceMonthlyCloseTest {

    @Mock
    private AttendsRepository attendsRepository;

    @Mock
    private TaskMonthlySalaryRepository monthlySalaryRepository;

    @Mock
    private StaffSalaryHistoryRepository salaryHistoryRepository;

    @Mock
    private StaffPositionHistoryRepository positionHistoryRepository;

    @Mock
    private PositionSalaryHistoryRepository positionSalaryHistoryRepository;

    @Mock
    private LawOfShiftAttendanceRepository lawRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ShiftAttendanceTypeRepository shiftRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AssignedTaskRepository assignedTaskRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private DepartmentScopeService departmentScopeService;

    @Test
    void closePayrollComputesDailySnapshotsBeforeMonthlyAggregation() {
        UUID staffId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        LocalDate day = LocalDate.of(2026, 5, 6);
        Person staff = person(staffId);
        ShiftAttendanceType shift = shift(shiftId);
        Department department = department(departmentId);
        Attends attends = attendance(staff, shift, day, new BigDecimal("2.00"));
        StaffSalaryHistory salary = salary(staff, new BigDecimal("100.00"),
                new BigDecimal("10000.00"));
        LawOfShiftAttendance law = law(shift, day, new BigDecimal("10.00"),
                new BigDecimal("5.00"));

        when(currentUserService.currentUser()).thenReturn(accountant());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(attendsRepository.findAll()).thenReturn(List.of(attends));
        when(departmentScopeService.staffBelongsToDepartment(staffId, departmentId))
                .thenReturn(true);
        when(monthlySalaryRepository.existsByStaffIdAndDepartmentIdAndYearAndMonth(
                staffId, departmentId, 2026, 5)).thenReturn(false);
        when(salaryHistoryRepository
                .findFirstByStaffIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        staffId, day))
                .thenReturn(Optional.of(salary));
        when(lawRepository.findFirstByShiftIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                shiftId, day))
                .thenReturn(Optional.of(law));
        when(attendsRepository.save(any(Attends.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(salaryHistoryRepository
                .findFirstByStaffIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        staffId, LocalDate.of(2026, 5, 31)))
                .thenReturn(Optional.of(salary));
        when(monthlySalaryRepository.save(any(TaskMonthlySalary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<TaskMonthlySalaryResponse> responses = service()
                .closeMonthlyPayroll(departmentId, 2026, 5, "EGP");

        ArgumentCaptor<Attends> attendsCaptor = ArgumentCaptor.forClass(Attends.class);
        verify(attendsRepository).save(attendsCaptor.capture());
        Attends savedAttendance = attendsCaptor.getValue();
        assertThat(savedAttendance.getDailySalary().getAmount()).isEqualByComparingTo("100.00");
        assertThat(savedAttendance.getDailyBonus().getAmount()).isEqualByComparingTo("20.00");
        assertThat(savedAttendance.getDailyDeduction().getAmount()).isEqualByComparingTo("0.00");
        assertThat(savedAttendance.getDailyNetSalary().getAmount()).isEqualByComparingTo("120.00");

        assertThat(responses).hasSize(1);
        TaskMonthlySalaryResponse response = responses.get(0);
        assertThat(response.monthlySalary().getAmount()).isEqualByComparingTo("100.00");
        assertThat(response.monthlyBonus().getAmount()).isEqualByComparingTo("20.00");
        assertThat(response.monthlyDeduction().getAmount()).isEqualByComparingTo("0.00");
        assertThat(response.tax().getAmount()).isEqualByComparingTo("10.00");
        assertThat(response.monthlyNetSalary().getAmount()).isEqualByComparingTo("110.00");
    }

    @Test
    void closePayrollRejectsAlreadyClosedStaffBeforeChangingAttendance() {
        UUID staffId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        LocalDate day = LocalDate.of(2026, 5, 6);
        Person staff = person(staffId);
        Attends attends = attendance(staff, shift(shiftId), day, BigDecimal.ZERO);

        when(currentUserService.currentUser()).thenReturn(accountant());
        when(departmentRepository.findById(departmentId))
                .thenReturn(Optional.of(department(departmentId)));
        when(attendsRepository.findAll()).thenReturn(List.of(attends));
        when(departmentScopeService.staffBelongsToDepartment(staffId, departmentId))
                .thenReturn(true);
        when(monthlySalaryRepository.existsByStaffIdAndDepartmentIdAndYearAndMonth(
                staffId, departmentId, 2026, 5)).thenReturn(true);

        assertThatThrownBy(() -> service().closeMonthlyPayroll(departmentId, 2026, 5, "EGP"))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                WorkforceErrorCode.PAYROLL_ALREADY_CLOSED));
        verify(attendsRepository, never()).save(any(Attends.class));
        verify(monthlySalaryRepository, never()).save(any(TaskMonthlySalary.class));
    }

    private PayrollService service() {
        return new PayrollService(
                attendsRepository,
                monthlySalaryRepository,
                salaryHistoryRepository,
                positionHistoryRepository,
                positionSalaryHistoryRepository,
                lawRepository,
                personRepository,
                shiftRepository,
                departmentRepository,
                assignedTaskRepository,
                currentUserService,
                departmentScopeService);
    }

    private static CurrentUser accountant() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.ACCOUNTANT, "accountant@example.com");
    }

    private static Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }

    private static ShiftAttendanceType shift(UUID id) {
        ShiftAttendanceType shift = ShiftAttendanceType.builder()
                .shiftName("Morning")
                .build();
        shift.setId(id);
        return shift;
    }

    private static Department department(UUID id) {
        Department department = new Department();
        department.setId(id);
        department.setDepartmentName("Operations");
        return department;
    }

    private static Attends attendance(Person staff, ShiftAttendanceType shift,
                                      LocalDate day, BigDecimal diffHour) {
        Attends attends = new Attends();
        attends.setStaff(staff);
        attends.setShift(shift);
        attends.setDate(day);
        attends.setIsAbsent(false);
        attends.setDiffHour(new HourDelta(diffHour));
        return attends;
    }

    private static StaffSalaryHistory salary(Person staff, BigDecimal baseDailyRate,
                                             BigDecimal maximumSalary) {
        StaffSalaryHistory salary = new StaffSalaryHistory();
        salary.setStaff(staff);
        salary.setEffectiveDate(LocalDate.of(2026, 5, 1));
        salary.setBaseDailyRate(baseDailyRate);
        salary.setMaximumSalary(maximumSalary);
        return salary;
    }

    private static LawOfShiftAttendance law(ShiftAttendanceType shift, LocalDate effectiveDate,
                                            BigDecimal extraBonus, BigDecimal diffDiscount) {
        LawOfShiftAttendance law = new LawOfShiftAttendance();
        law.setShift(shift);
        law.setEffectiveDate(effectiveDate);
        law.setExpectedHours(new BigDecimal("8.00"));
        law.setOneHourExtraBonus(new Money(extraBonus, "EGP"));
        law.setOneHourDiffDiscount(new Money(diffDiscount, "EGP"));
        return law;
    }
}
