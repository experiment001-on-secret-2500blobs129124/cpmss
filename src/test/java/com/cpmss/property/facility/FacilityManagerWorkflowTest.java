package com.cpmss.property.facility;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.building.BuildingRepository;
import com.cpmss.property.common.PropertyErrorCode;
import com.cpmss.property.facilitymanager.FacilityManager;
import com.cpmss.property.facilitymanager.FacilityManagerRepository;
import com.cpmss.property.facilitymanager.dto.FacilityManagerResponse;
import com.cpmss.property.facilityhourshistory.FacilityHoursHistoryRepository;
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
 * Verifies the current facility-manager assignment lookup.
 */
@ExtendWith(MockitoExtension.class)
class FacilityManagerWorkflowTest {

    @Mock
    private FacilityRepository repository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private FacilityHoursHistoryRepository hoursHistoryRepository;

    @Mock
    private FacilityManagerRepository managerRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FacilityMapper mapper;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void returnsCurrentFacilityManager() {
        UUID facilityId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2026, 5, 1);
        when(currentUserService.currentUser()).thenReturn(facilityOfficer());
        when(repository.existsById(facilityId)).thenReturn(true);
        when(managerRepository
                .findFirstByFacilityIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
                        facilityId))
                .thenReturn(Optional.of(managerAssignment(facilityId, managerId, start)));

        FacilityManagerResponse response = service().getCurrentManager(facilityId);

        assertThat(response.facilityId()).isEqualTo(facilityId);
        assertThat(response.managerId()).isEqualTo(managerId);
        assertThat(response.managementStartDate()).isEqualTo(start);
        assertThat(response.managementEndDate()).isNull();
    }

    @Test
    void rejectsMissingCurrentFacilityManager() {
        UUID facilityId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(facilityOfficer());
        when(repository.existsById(facilityId)).thenReturn(true);
        when(managerRepository
                .findFirstByFacilityIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
                        facilityId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().getCurrentManager(facilityId))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(PropertyErrorCode.FACILITY_MANAGER_NOT_FOUND));
    }

    private FacilityService service() {
        return new FacilityService(repository, buildingRepository, companyRepository,
                hoursHistoryRepository, managerRepository, personRepository, mapper,
                currentUserService);
    }

    private static CurrentUser facilityOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.FACILITY_OFFICER, "facility@example.com");
    }

    private static FacilityManager managerAssignment(
            UUID facilityId, UUID managerId, LocalDate start) {
        Facility facility = new Facility();
        facility.setId(facilityId);
        Person manager = Person.builder().firstName("A").lastName("B").build();
        manager.setId(managerId);
        FacilityManager assignment = new FacilityManager();
        assignment.setFacility(facility);
        assignment.setManager(manager);
        assignment.setManagementStartDate(start);
        return assignment;
    }
}
