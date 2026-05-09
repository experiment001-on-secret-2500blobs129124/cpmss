package com.cpmss.security.entersat;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Business rules for {@link EntersAt} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class EntersAtRules {

    /**
     * Validates that exactly one entry method is set (permit XOR manual plate).
     *
     * @param permitId        the permit UUID (may be {@code null})
     * @param manualPlateEntry the manually entered plate (may be {@code null})
     * @throws ApiException if neither or both are set
     */
    public void validateExactlyOneEntryMethod(UUID permitId, String manualPlateEntry) {
        boolean hasPermit = permitId != null;
        boolean hasManualPlate = manualPlateEntry != null && !manualPlateEntry.isBlank();

        if (hasPermit == hasManualPlate) {
            throw new ApiException(SecurityErrorCode.GUARD_NOT_ASSIGNED);
        }
    }
}
