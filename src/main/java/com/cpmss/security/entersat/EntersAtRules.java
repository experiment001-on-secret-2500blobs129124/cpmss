package com.cpmss.security.entersat;

import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ForbiddenException;

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

    /**
     * Validates that a gate guard is assigned to the gate they are logging.
     *
     * <p>Security officers and higher roles can administer entries broadly;
     * this guard-only check protects the operational rule that a guard can log
     * events only for the gate where they are currently posted.
     *
     * @param actorRole           the current user's system role
     * @param assignedToGateAtTime whether the guard has an active gate posting
     * @throws ForbiddenException if a gate guard is not assigned to the gate
     */
    public void validateGateGuardAssignedToGate(SystemRole actorRole,
                                                boolean assignedToGateAtTime) {
        if (actorRole == SystemRole.GATE_GUARD && !assignedToGateAtTime) {
            throw new ForbiddenException("Gate guard is not assigned to this gate");
        }
    }

    /**
     * Validates that a gate guard cannot spoof the processing guard.
     *
     * <p>When a gate guard supplies {@code processedById}, it must be their
     * linked person id. The service may still default anonymous entries to the
     * current guard when the request leaves the field blank.
     *
     * @param actorRole      the current user's system role
     * @param actorPersonId  linked person UUID for the current user
     * @param processedById  requested processing guard UUID
     * @throws ForbiddenException if a gate guard tries to act as another guard
     */
    public void validateGateGuardProcessesOnlySelf(SystemRole actorRole,
                                                   UUID actorPersonId,
                                                   UUID processedById) {
        if (actorRole == SystemRole.GATE_GUARD
                && processedById != null
                && !processedById.equals(actorPersonId)) {
            throw new ForbiddenException("Gate guards can only process entries as themselves");
        }
    }
}
