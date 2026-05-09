package com.cpmss.security.vehicle;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.PermitStatus;
import com.cpmss.security.accesspermit.PermitType;
import com.cpmss.security.common.SecurityErrorCode;

import java.util.UUID;

/**
 * Business rules for {@link Vehicle} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see VehicleService
 */
public class VehicleRules {

    /**
     * Validates that exactly one owner is set.
     *
     * <p>A vehicle must be owned by exactly one of: a person,
     * a department, or a company. Setting zero or more than one
     * owner violates the mutual exclusion constraint.
     *
     * @param ownerPersonId     the person owner UUID (may be {@code null})
     * @param ownerDepartmentId the department owner UUID (may be {@code null})
     * @param ownerCompanyId    the company owner UUID (may be {@code null})
     * @throws ApiException if zero or more than one owner is set
     */
    public void validateExactlyOneOwner(UUID ownerPersonId, UUID ownerDepartmentId,
                                        UUID ownerCompanyId) {
        int count = 0;
        if (ownerPersonId != null) {
            count++;
        }
        if (ownerDepartmentId != null) {
            count++;
        }
        if (ownerCompanyId != null) {
            count++;
        }

        if (count != 1) {
            throw new ApiException(SecurityErrorCode.VEHICLE_OWNER_INVALID);
        }
    }

    /**
     * Validates that a license number is unique system-wide.
     *
     * @param licenseNo the desired license number
     * @param exists    whether a vehicle with this license already exists
     * @throws ApiException if the license number is already registered
     */
    public void validateLicenseNoUnique(String licenseNo, boolean exists) {
        if (exists) {
            throw new ApiException(SecurityErrorCode.VEHICLE_LICENSE_DUPLICATE);
        }
    }

    /**
     * Validates that an access permit can be linked to a vehicle.
     *
     * <p>The vehicle-permit bridge represents physical vehicle stickers, not
     * person badges or visitor passes. Only active vehicle sticker permits are
     * eligible for the link workflow.
     *
     * @param permit the access permit being linked
     * @throws ApiException if the permit cannot be linked to a vehicle
     */
    public void validatePermitCanBeLinkedToVehicle(AccessPermit permit) {
        if (permit.getPermitTypeValue() != PermitType.VEHICLE_STICKER) {
            throw new ApiException(SecurityErrorCode.VEHICLE_PERMIT_TYPE_INVALID);
        }
        if (permit.getPermitStatusValue() != PermitStatus.ACTIVE) {
            throw new ApiException(SecurityErrorCode.VEHICLE_PERMIT_NOT_ACTIVE);
        }
    }

    /**
     * Validates that a vehicle-permit link can be created.
     *
     * @param alreadyLinked whether the vehicle already has this permit linked
     * @throws ApiException if the link already exists
     */
    public void validatePermitNotAlreadyLinked(boolean alreadyLinked) {
        if (alreadyLinked) {
            throw new ApiException(SecurityErrorCode.VEHICLE_PERMIT_ALREADY_LINKED);
        }
    }

    /**
     * Validates that a vehicle-permit link exists before unlinking.
     *
     * @param linked whether the vehicle currently has this permit linked
     * @throws ApiException if the link does not exist
     */
    public void validatePermitLinked(boolean linked) {
        if (!linked) {
            throw new ApiException(SecurityErrorCode.VEHICLE_PERMIT_NOT_LINKED);
        }
    }
}
