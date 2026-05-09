package com.cpmss.security.vehicle;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.AccessPermitRepository;
import com.cpmss.security.accesspermit.PermitStatus;
import com.cpmss.security.accesspermit.PermitType;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.security.vehicle.dto.VehiclePermitLinkResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies the parent-managed vehicle-permit link workflow.
 *
 * <p>Security route authorization lets a security administrator reach the
 * endpoint. The service still validates that both records exist and that the
 * permit is an active vehicle sticker before mutating the join table owner.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServicePermitLinkTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AccessPermitRepository accessPermitRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void linksActiveVehicleStickerPermit() {
        UUID vehicleId = UUID.randomUUID();
        UUID permitId = UUID.randomUUID();
        Vehicle vehicle = vehicle(vehicleId);
        AccessPermit permit = permit(permitId, PermitType.VEHICLE_STICKER, PermitStatus.ACTIVE);
        when(currentUserService.currentUser()).thenReturn(securityOfficer());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(accessPermitRepository.findById(permitId)).thenReturn(Optional.of(permit));

        VehiclePermitLinkResponse response = service().linkPermit(vehicleId, permitId);

        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertThat(vehicleCaptor.getValue().getPermits()).containsExactly(permit);
        assertThat(response.vehicleId()).isEqualTo(vehicleId);
        assertThat(response.permitId()).isEqualTo(permitId);
        assertThat(response.licenseNo()).isEqualTo("ABC-123");
        assertThat(response.permitNo()).isEqualTo("VS-001");
    }

    @Test
    void rejectsDuplicateVehiclePermitLink() {
        UUID vehicleId = UUID.randomUUID();
        UUID permitId = UUID.randomUUID();
        Vehicle vehicle = vehicle(vehicleId);
        AccessPermit permit = permit(permitId, PermitType.VEHICLE_STICKER, PermitStatus.ACTIVE);
        vehicle.getPermits().add(permit);
        when(currentUserService.currentUser()).thenReturn(securityOfficer());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(accessPermitRepository.findById(permitId)).thenReturn(Optional.of(permit));

        assertThatThrownBy(() -> service().linkPermit(vehicleId, permitId))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_ALREADY_LINKED));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void rejectsNonVehicleStickerPermit() {
        UUID vehicleId = UUID.randomUUID();
        UUID permitId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(securityOfficer());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle(vehicleId)));
        when(accessPermitRepository.findById(permitId)).thenReturn(Optional.of(
                permit(permitId, PermitType.STAFF_BADGE, PermitStatus.ACTIVE)));

        assertThatThrownBy(() -> service().linkPermit(vehicleId, permitId))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_TYPE_INVALID));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void unlinksExistingVehiclePermit() {
        UUID vehicleId = UUID.randomUUID();
        UUID permitId = UUID.randomUUID();
        Vehicle vehicle = vehicle(vehicleId);
        AccessPermit permit = permit(permitId, PermitType.VEHICLE_STICKER, PermitStatus.ACTIVE);
        vehicle.getPermits().add(permit);
        when(currentUserService.currentUser()).thenReturn(securityOfficer());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(accessPermitRepository.findById(permitId)).thenReturn(Optional.of(permit));

        service().unlinkPermit(vehicleId, permitId);

        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertThat(vehicleCaptor.getValue().getPermits()).isEmpty();
    }

    @Test
    void rejectsUnlinkingMissingVehiclePermitLink() {
        UUID vehicleId = UUID.randomUUID();
        UUID permitId = UUID.randomUUID();
        when(currentUserService.currentUser()).thenReturn(securityOfficer());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle(vehicleId)));
        when(accessPermitRepository.findById(permitId)).thenReturn(Optional.of(
                permit(permitId, PermitType.VEHICLE_STICKER, PermitStatus.ACTIVE)));

        assertThatThrownBy(() -> service().unlinkPermit(vehicleId, permitId))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_NOT_LINKED));
        verify(vehicleRepository, never()).save(any());
    }

    private VehicleService service() {
        return new VehicleService(
                vehicleRepository,
                personRepository,
                departmentRepository,
                companyRepository,
                accessPermitRepository,
                currentUserService,
                vehicleMapper);
    }

    private static CurrentUser securityOfficer() {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(),
                SystemRole.SECURITY_OFFICER, "security@example.com");
    }

    private static Vehicle vehicle(UUID id) {
        Vehicle vehicle = Vehicle.builder()
                .licenseNo(LicensePlate.of("ABC-123"))
                .build();
        vehicle.setId(id);
        return vehicle;
    }

    private static AccessPermit permit(UUID id, PermitType type, PermitStatus status) {
        AccessPermit permit = new AccessPermit();
        permit.setId(id);
        permit.setPermitNo("VS-001");
        permit.setPermitType(type);
        permit.setPermitStatus(status);
        return permit;
    }
}
