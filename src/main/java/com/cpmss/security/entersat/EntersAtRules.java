package com.cpmss.security.entersat;

import com.cpmss.platform.exception.BusinessException;

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
     * @throws BusinessException if neither or both are set
     */
    public void validateExactlyOneEntryMethod(UUID permitId, String manualPlateEntry) {
        boolean hasPermit = permitId != null;
        boolean hasManualPlate = manualPlateEntry != null && !manualPlateEntry.isBlank();

        if (hasPermit == hasManualPlate) {
            throw new BusinessException(
                    "Exactly one of permit or manual plate entry must be provided");
        }
    }
}
