package com.cpmss.hr.staffposition;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.staffposition.dto.CreatePositionSalaryHistoryRequest;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffpositionhistory.dto.CreateStaffPositionHistoryRequest;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
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

@ExtendWith(MockitoExtension.class)
class StaffPositionHistoryWorkflowTest {

    @Mock
    private StaffPositionRepository positionRepository;

    @Mock
    private PositionSalaryHistoryRepository salaryHistoryRepository;

    @Mock
    private StaffPositionHistoryRepository positionHistoryRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private StaffPositionMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void assignmentClosesPreviousCurrentPositionRow() {
        UUID staffId = UUID.randomUUID();
        UUID oldPositionId = UUID.randomUUID();
        UUID newPositionId = UUID.randomUUID();
        UUID authorizerId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 5, 1);
        Person staff = person(staffId);
        Person authorizer = person(authorizerId);
        StaffPosition newPosition = position(newPositionId);
        StaffPositionHistory current = new StaffPositionHistory();
        current.setPerson(staff);
        current.setPosition(position(oldPositionId));
        current.setEffectiveDate(LocalDate.of(2025, 1, 1));

        when(currentUserService.currentUser()).thenReturn(currentUser(SystemRole.HR_OFFICER));
        when(positionHistoryRepository.existsByPersonIdAndPositionIdAndEffectiveDate(
                staffId, newPositionId, effectiveDate)).thenReturn(false);
        when(personRepository.findById(staffId)).thenReturn(Optional.of(staff));
        when(personRepository.findById(authorizerId)).thenReturn(Optional.of(authorizer));
        when(positionRepository.findById(newPositionId)).thenReturn(Optional.of(newPosition));
        when(positionHistoryRepository.findByPersonIdAndEndDateIsNull(staffId))
                .thenReturn(Optional.of(current));
        when(positionHistoryRepository.save(any(StaffPositionHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StaffPositionService service = new StaffPositionService(
                positionRepository,
                salaryHistoryRepository,
                positionHistoryRepository,
                departmentRepository,
                personRepository,
                mapper,
                currentUserService);

        service.assignStaffPosition(new CreateStaffPositionHistoryRequest(
                staffId, newPositionId, effectiveDate, authorizerId));

        assertThat(current.getEndDate()).isEqualTo(effectiveDate.minusDays(1));
    }

    @Test
    void positionSalaryHistoryRejectsNonPositiveSalaryBandAmounts() {
        UUID positionId = UUID.randomUUID();
        LocalDate effectiveDate = LocalDate.of(2026, 5, 1);
        when(currentUserService.currentUser()).thenReturn(currentUser(SystemRole.HR_OFFICER));
        when(salaryHistoryRepository.existsByPositionIdAndSalaryEffectiveDate(
                positionId, effectiveDate)).thenReturn(false);
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position(positionId)));

        StaffPositionService service = new StaffPositionService(
                positionRepository,
                salaryHistoryRepository,
                positionHistoryRepository,
                departmentRepository,
                personRepository,
                mapper,
                currentUserService);

        assertThatThrownBy(() -> service.createPositionSalaryHistory(
                positionId,
                new CreatePositionSalaryHistoryRequest(
                        effectiveDate, BigDecimal.ZERO, new BigDecimal("120.00"))))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(HrErrorCode.SALARY_NOT_POSITIVE));

        verify(salaryHistoryRepository, never()).save(any(PositionSalaryHistory.class));
    }

    private CurrentUser currentUser(SystemRole role) {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(), role, "hr@example.com");
    }

    private Person person(UUID id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }

    private StaffPosition position(UUID id) {
        StaffPosition position = new StaffPosition();
        position.setId(id);
        return position;
    }
}
