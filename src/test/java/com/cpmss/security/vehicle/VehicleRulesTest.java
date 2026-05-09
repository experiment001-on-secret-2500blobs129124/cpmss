package com.cpmss.security.vehicle;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.PermitStatus;
import com.cpmss.security.accesspermit.PermitType;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies vehicle business rules.
 *
 * <p>The vehicle-permit workflow allows only active vehicle sticker permits to
 * be attached to registered vehicles.
 */
class VehicleRulesTest {

    private final VehicleRules rules = new VehicleRules();

    @Test
    void allowsActiveVehicleStickerPermit() {
        rules.validatePermitCanBeLinkedToVehicle(
                permit(PermitType.VEHICLE_STICKER, PermitStatus.ACTIVE));
    }

    @Test
    void rejectsNonVehicleStickerPermit() {
        assertThatThrownBy(() -> rules.validatePermitCanBeLinkedToVehicle(
                permit(PermitType.STAFF_BADGE, PermitStatus.ACTIVE)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_TYPE_INVALID));
    }

    @Test
    void rejectsInactiveVehicleStickerPermit() {
        assertThatThrownBy(() -> rules.validatePermitCanBeLinkedToVehicle(
                permit(PermitType.VEHICLE_STICKER, PermitStatus.REVOKED)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_NOT_ACTIVE));
    }

    @Test
    void rejectsDuplicateVehiclePermitLink() {
        assertThatThrownBy(() -> rules.validatePermitNotAlreadyLinked(true))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_ALREADY_LINKED));
    }

    @Test
    void rejectsMissingVehiclePermitLink() {
        assertThatThrownBy(() -> rules.validatePermitLinked(false))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode())
                                .isEqualTo(SecurityErrorCode.VEHICLE_PERMIT_NOT_LINKED));
    }

    private static AccessPermit permit(PermitType type, PermitStatus status) {
        AccessPermit permit = new AccessPermit();
        permit.setPermitType(type);
        permit.setPermitStatus(status);
        return permit;
    }
}
