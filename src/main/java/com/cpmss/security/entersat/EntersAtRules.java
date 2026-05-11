package com.cpmss.security.entersat;

import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.PermitStatus;
import com.cpmss.security.common.SecurityErrorCode;

import java.time.LocalDate;

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

    /**
     * Validates that a gate guard is assigned to the gate they are logging.
     *
     * <p>Security officers and higher roles can administer entries broadly;
     * this guard-only check protects the operational rule that a guard can log
     * events only for the gate where they are currently posted.
     *
     * @param actorRole            the current user's system role
     * @param assignedToGateAtTime whether the guard has an active gate posting
     * @throws ApiException if a gate guard is not assigned to the gate
     */
    public void validateGateGuardAssignedToGate(SystemRole actorRole,
                                                boolean assignedToGateAtTime) {
        if (actorRole == SystemRole.GATE_GUARD && !assignedToGateAtTime) {
            throw new ApiException(SecurityErrorCode.GUARD_NOT_ASSIGNED);
        }
    }

    /**
     * Validates that a gate guard cannot spoof the processing guard.
     *
     * <p>When a gate guard supplies {@code processedById}, it must be their
     * linked person id. The service may still default anonymous entries to the
     * current guard when the request leaves the field blank.
     *
     * @param actorRole     the current user's system role
     * @param actorPersonId linked person UUID for the current user
     * @param processedById requested processing guard UUID
     * @throws ApiException if a gate guard tries to act as another guard
     */
    public void validateGateGuardProcessesOnlySelf(SystemRole actorRole,
                                                   UUID actorPersonId,
                                                   UUID processedById) {
        if (actorRole == SystemRole.GATE_GUARD
                && processedById != null
                && !processedById.equals(actorPersonId)) {
            throw new ApiException(SecurityErrorCode.GUARD_NOT_ASSIGNED);
        }
    }
    /**
     * Validates that the permit is usable at the gate entry date.
     *
     * <p>A permit-based gate entry is a business action, not a raw FK insert:
     * the referenced permit must be active, unexpired for the event date, and
     * owned by a person who is not blacklisted.
     *
     * @param permit the referenced access permit
     * @param entryDate the date of the gate event
     * @throws ApiException if the permit cannot be used for entry
     */
    public void validatePermitUsableForEntry(AccessPermit permit, LocalDate entryDate) {
        if (permit == null) {
            return;
        }
        if (permit.getPermitStatusValue() != PermitStatus.ACTIVE) {
            throw new ApiException(SecurityErrorCode.ACCESS_PERMIT_NOT_ACTIVE);
        }
        if (permit.getExpiryDate() != null && permit.getExpiryDate().isBefore(entryDate)) {
            throw new ApiException(SecurityErrorCode.ACCESS_PERMIT_EXPIRED);
        }
        if (permit.getPermitHolder() != null
                && Boolean.TRUE.equals(permit.getPermitHolder().getIsBlacklisted())) {
            throw new ApiException(SecurityErrorCode.ACCESS_PERMIT_HOLDER_BLACKLISTED);
        }
    }

}
