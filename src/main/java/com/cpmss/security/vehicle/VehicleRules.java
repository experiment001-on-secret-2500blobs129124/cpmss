package com.cpmss.security.vehicle;

import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ConflictException;

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
     * @throws BusinessException if zero or more than one owner is set
     */
    public void validateExactlyOneOwner(UUID ownerPersonId, UUID ownerDepartmentId, UUID ownerCompanyId) {
        int count = 0;
        if (ownerPersonId != null) count++;
        if (ownerDepartmentId != null) count++;
        if (ownerCompanyId != null) count++;

        if (count != 1) {
            throw new BusinessException(
                    "A vehicle must have exactly one owner (person, department, or company)");
        }
    }

    /**
     * Validates that a license number is unique system-wide.
     *
     * @param licenseNo the desired license number
     * @param exists    whether a vehicle with this license already exists
     * @throws ConflictException if the license number is already registered
     */
    public void validateLicenseNoUnique(String licenseNo, boolean exists) {
        if (exists) {
            throw new ConflictException("License number '" + licenseNo + "' is already registered");
        }
    }
}
