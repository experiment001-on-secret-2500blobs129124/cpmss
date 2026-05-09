package com.cpmss.leasing.contract;

import com.cpmss.leasing.common.LeasingErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Business rules for {@link Contract} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see ContractService
 */
public class ContractRules {

    /**
     * Validates that exactly one target is set (unit XOR facility).
     *
     * <p>A contract must cover exactly one target — either a unit
     * (Residential) or a facility (Commercial).
     *
     * @param unitId     the unit UUID (may be {@code null})
     * @param facilityId the facility UUID (may be {@code null})
     * @throws ApiException if zero or both targets are set
     */
    public void validateExactlyOneTarget(UUID unitId, UUID facilityId) {
        int count = 0;
        if (unitId != null) count++;
        if (facilityId != null) count++;

        if (count != 1) {
            throw new ApiException(LeasingErrorCode.CONTRACT_TARGET_INVALID);
        }
    }

    /**
     * Validates that a contract reference is unique system-wide.
     *
     * @param contractReference the desired reference
     * @param exists            whether a contract with this reference already exists
     * @throws ApiException if the reference is already registered
     */
    public void validateReferenceUnique(String contractReference, boolean exists) {
        if (exists) {
            throw new ApiException(LeasingErrorCode.CONTRACT_REFERENCE_DUPLICATE);
        }
    }
}
