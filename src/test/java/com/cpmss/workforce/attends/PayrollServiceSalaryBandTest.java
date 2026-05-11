package com.cpmss.workforce.attends;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendanceRepository;
import com.cpmss.hr.staffposition.PositionSalaryHistory;
import com.cpmss.hr.staffposition.PositionSalaryHistoryRepository;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistory;
import com.cpmss.hr.staffsalaryhistory.StaffSalaryHistoryRepository;
import com.cpmss.hr.staffsalaryhistory.dto.CreateStaffSalaryHistoryRequest;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.assignedtask.AssignedTaskRepository;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceTypeRepository;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies salary-change checks against active position salary bands.
 */
@ExtendWith(MockitoExtension.class)
class PayrollServiceSalaryBandTest {

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
    void salaryChangeRejectsMaximumAboveActivePositionBand() {
        UUID staffId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 4, 1);
        StaffPosition position = new StaffPosition();
        position.setId(positionId);
        StaffPositionHistory currentPosition = new StaffPositionHistory();
        currentPosition.setPosition(position);
        PositionSalaryHistory activeBand = new PositionSalaryHistory();
        activeBand.setMaximumSalary(new BigDecimal("5000.00"));
        activeBand.setBaseDailyRate(new BigDecimal("150.00"));

        when(currentUserService.currentUser()).thenReturn(new CurrentUser(
                UUID.randomUUID(), UUID.randomUUID(), SystemRole.HR_OFFICER,
                "hr@example.com"));
        when(positionHistoryRepository.findByPersonIdAndEndDateIsNull(staffId))
                .thenReturn(Optional.of(currentPosition));
        when(positionSalaryHistoryRepository
                .findFirstByPositionIdAndSalaryEffectiveDateLessThanEqualOrderBySalaryEffectiveDateDesc(
                        positionId, effectiveDate))
                .thenReturn(Optional.of(activeBand));

        assertThatThrownBy(() -> service().createSalaryChange(
                new CreateStaffSalaryHistoryRequest(
                        staffId,
                        effectiveDate,
                        new BigDecimal("200.00"),
                        new BigDecimal("6000.00"),
                        null,
                        null)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                HrErrorCode.STAFF_SALARY_EXCEEDS_POSITION_MAX));

        verify(salaryHistoryRepository, never()).save(any(StaffSalaryHistory.class));
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
}
