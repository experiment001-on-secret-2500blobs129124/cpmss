package com.cpmss.organization.department;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.departmentlocationhistory.DepartmentLocationHistoryRepository;
import com.cpmss.organization.departmentmanagers.DepartmentManagers;
import com.cpmss.organization.departmentmanagers.DepartmentManagersRepository;
import com.cpmss.organization.departmentmanagers.dto.DeptManagerResponse;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.building.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Verifies current department-manager assignment lookups.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentManagerWorkflowTest {

    @Mock
    private DepartmentRepository repository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private DepartmentLocationHistoryRepository locationHistoryRepository;

    @Mock
    private DepartmentManagersRepository managersRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private DepartmentScopeService departmentScopeService;

    @Test
    void returnsCurrentDepartmentManager() {
        UUID departmentId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2026, 5, 1);
        when(currentUserService.currentUser()).thenReturn(hrOfficer());
        when(repository.existsById(departmentId)).thenReturn(true);
        when(managersRepository
                .findFirstByDepartmentIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
                        departmentId))
                .thenReturn(Optional.of(managerAssignment(departmentId, managerId, start)));

        DeptManagerResponse response = service().getCurrentManager(departmentId);

        assertThat(response.departmentId()).isEqualTo(departmentId);
        assertThat(response.managerId()).isEqualTo(managerId);
        assertThat(response.managementStartDate()).isEqualTo(start);
        assertThat(response.managementEndDate()).isNull();
    }

    @Test
    void rejectsMissingCurrentDepartmentManager() {
        UUID departmentId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(hrOfficer());
        when(repository.existsById(departmentId)).thenReturn(true);
        when(managersRepository
                .findFirstByDepartmentIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
                        departmentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().getCurrentManager(departmentId))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(OrganizationErrorCode.DEPARTMENT_MANAGER_NOT_FOUND));
    }

    private DepartmentService service() {
        return new DepartmentService(repository, buildingRepository, locationHistoryRepository,
                managersRepository, personRepository, mapper, currentUserService,
                departmentScopeService);
    }

    private static CurrentUser hrOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.HR_OFFICER, "hr@example.com");
    }

    private static DepartmentManagers managerAssignment(
            UUID departmentId, UUID managerId, LocalDate start) {
        Department department = new Department();
        department.setId(departmentId);
        Person manager = Person.builder().firstName("A").lastName("B").build();
        manager.setId(managerId);
        DepartmentManagers assignment = new DepartmentManagers();
        assignment.setDepartment(department);
        assignment.setManager(manager);
        assignment.setManagementStartDate(start);
        return assignment;
    }
}
