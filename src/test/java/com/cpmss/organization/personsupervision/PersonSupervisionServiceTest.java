package com.cpmss.organization.personsupervision;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.personsupervision.dto.CreatePersonSupervisionRequest;
import com.cpmss.organization.personsupervision.dto.PersonSupervisionResponse;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies supervisor-to-staff relationship ownership rules.
 */
@ExtendWith(MockitoExtension.class)
class PersonSupervisionServiceTest {

    @Mock
    private PersonSupervisionRepository repository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentScopeService departmentScopeService;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void createsSupervisionRelationship() {
        UUID supervisorId = UUID.randomUUID();
        UUID superviseeId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2026, 5, 1);
        when(currentUserService.currentUser()).thenReturn(hrOfficer());
        when(repository.findBySupervisorIdAndSuperviseeIdAndSupervisionStartDate(
                supervisorId, superviseeId, start)).thenReturn(Optional.empty());
        when(personRepository.findById(supervisorId)).thenReturn(Optional.of(person(supervisorId)));
        when(personRepository.findById(superviseeId)).thenReturn(Optional.of(person(superviseeId)));
        when(repository.save(any(PersonSupervision.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PersonSupervisionResponse response = service().create(
                new CreatePersonSupervisionRequest(supervisorId, superviseeId, start, "Ops"));

        ArgumentCaptor<PersonSupervision> supervisionCaptor =
                ArgumentCaptor.forClass(PersonSupervision.class);
        verify(repository).save(supervisionCaptor.capture());
        assertThat(supervisionCaptor.getValue().getTeamName()).isEqualTo("Ops");
        assertThat(response.supervisorId()).isEqualTo(supervisorId);
        assertThat(response.superviseeId()).isEqualTo(superviseeId);
    }

    @Test
    void rejectsSelfSupervision() {
        UUID personId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(hrOfficer());

        assertThatThrownBy(() -> service().create(new CreatePersonSupervisionRequest(
                personId, personId, LocalDate.of(2026, 5, 1), "Ops")))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(OrganizationErrorCode.SELF_SUPERVISION_FORBIDDEN));
    }

    @Test
    void supervisorSeesOnlyDirectActiveSupervisees() {
        UUID supervisorId = UUID.randomUUID();
        UUID superviseeId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(supervisor(supervisorId));
        when(repository.findBySupervisorIdAndSupervisionEndDateIsNull(supervisorId))
                .thenReturn(List.of(supervision(supervisorId, superviseeId)));

        List<PersonSupervisionResponse> response = service().getActiveSupervisees(supervisorId);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).superviseeId()).isEqualTo(superviseeId);
    }

    @Test
    void departmentManagerSeesDepartmentScopedSupervisionRows() {
        UUID managerPersonId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();
        UUID superviseeId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        CurrentUser manager = departmentManager(managerPersonId);
        when(currentUserService.currentUser()).thenReturn(manager);
        when(repository.findBySuperviseeIdAndSupervisionEndDateIsNull(superviseeId))
                .thenReturn(List.of(supervision(supervisorId, superviseeId)));
        when(departmentScopeService.activeDepartmentForStaff(superviseeId))
                .thenReturn(Optional.of(departmentId));
        when(departmentScopeService.managesDepartment(manager, departmentId)).thenReturn(true);

        List<PersonSupervisionResponse> response = service().getActiveSupervisors(superviseeId);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).supervisorId()).isEqualTo(supervisorId);
    }

    private PersonSupervisionService service() {
        return new PersonSupervisionService(repository, personRepository,
                departmentScopeService, currentUserService);
    }

    private static CurrentUser hrOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.HR_OFFICER, "hr@example.com");
    }

    private static CurrentUser supervisor(UUID personId) {
        return new CurrentUser(UUID.randomUUID(), personId,
                SystemRole.SUPERVISOR, "supervisor@example.com");
    }

    private static CurrentUser departmentManager(UUID personId) {
        return new CurrentUser(UUID.randomUUID(), personId,
                SystemRole.DEPARTMENT_MANAGER, "manager@example.com");
    }

    private static PersonSupervision supervision(UUID supervisorId, UUID superviseeId) {
        PersonSupervision supervision = new PersonSupervision();
        supervision.setSupervisor(person(supervisorId));
        supervision.setSupervisee(person(superviseeId));
        supervision.setSupervisionStartDate(LocalDate.of(2026, 5, 1));
        return supervision;
    }

    private static Person person(UUID id) {
        Person person = Person.builder().firstName("A").lastName("B").build();
        person.setId(id);
        return person;
    }
}
