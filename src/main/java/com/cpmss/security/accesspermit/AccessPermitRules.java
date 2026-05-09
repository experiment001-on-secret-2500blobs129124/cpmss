package com.cpmss.security.accesspermit;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Business rules for {@link AccessPermit} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class AccessPermitRules {

    /**
     * Validates that exactly one entitlement basis is set.
     *
     * <p>A permit must reference exactly one of: staff profile,
     * contract, work order, or inviting person.
     *
     * @param staffProfileId the staff profile UUID (may be {@code null})
     * @param contractId     the contract UUID (may be {@code null})
     * @param workOrderId    the work order UUID (may be {@code null})
     * @param invitedById    the inviting person UUID (may be {@code null})
     * @throws ApiException if zero or more than one basis is set
     */
    public void validateExactlyOneEntitlement(UUID staffProfileId, UUID contractId,
                                              UUID workOrderId, UUID invitedById) {
        int count = 0;
        if (staffProfileId != null) count++;
        if (contractId != null) count++;
        if (workOrderId != null) count++;
        if (invitedById != null) count++;

        if (count != 1) {
            throw new ApiException(SecurityErrorCode.PERMIT_ALREADY_ACTIVE);
        }
    }
}
