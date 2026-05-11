package com.cpmss.security.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.gateguardassignment.GateGuardAssignment;

import java.time.Instant;
import java.util.UUID;

/**
 * Service-level ownership rules for physical security resources.
 *
 * <p>Route guards decide which roles can reach a controller. These rules keep
 * the bounded context safe when a broad route still needs record-specific
 * ownership checks.
 */
public class SecurityAccessRules {

    /**
     * Validates that the current user can manage broad security records.
     *
     * @param currentUser authenticated user
     * @throws ApiException if the user is not a security administrator
     */
    public void validateSecurityAdministrator(CurrentUser currentUser) {
        if (!isSecurityAdministrator(currentUser)) {
            throw new ApiException(SecurityErrorCode.SECURITY_RECORD_ACCESS_DENIED);
        }
    }

    /**
     * Validates access to a single access permit lookup.
     *
     * <p>Security administrators can administer and inspect all permits. Gate
     * guards can read individual permit records for checkpoint verification,
     * but broad listing remains restricted to security administration.
     *
     * @param currentUser authenticated user
     * @throws ApiException if the user cannot read individual permits
     */
    public void validateCanReadAccessPermit(CurrentUser currentUser) {
        if (isSecurityAdministrator(currentUser)
                || currentUser.hasRole(SystemRole.GATE_GUARD)) {
            return;
        }
        throw new ApiException(SecurityErrorCode.SECURITY_RECORD_ACCESS_DENIED);
    }

    /**
     * Validates access to a specific guard assignment.
     *
     * <p>Security administrators can inspect every assignment. A gate guard can
     * inspect only their own active assignment.
     *
     * @param currentUser authenticated user
     * @param assignment  assignment being read
     * @param at          timestamp used to determine whether the assignment is active
     * @throws ApiException if the user does not own the assignment
     */
    public void validateCanReadGuardAssignment(CurrentUser currentUser,
                                               GateGuardAssignment assignment,
                                               Instant at) {
        if (isSecurityAdministrator(currentUser)) {
            return;
        }
        if (currentUser.hasRole(SystemRole.GATE_GUARD)
                && isOwnActiveAssignment(currentUser, assignment, at)) {
            return;
        }
        throw new ApiException(SecurityErrorCode.SECURITY_RECORD_ACCESS_DENIED);
    }

    /**
     * Checks whether a user has broad security administration authority.
     *
     * @param currentUser authenticated user
     * @return true for admin, general manager, or security officer
     */
    public boolean isSecurityAdministrator(CurrentUser currentUser) {
        return currentUser.hasRole(SystemRole.ADMIN)
                || currentUser.hasRole(SystemRole.GENERAL_MANAGER)
                || currentUser.hasRole(SystemRole.SECURITY_OFFICER);
    }

    private boolean isOwnActiveAssignment(CurrentUser currentUser,
                                          GateGuardAssignment assignment,
                                          Instant at) {
        UUID guardId = currentUser.requirePersonId("Viewing gate guard assignments");
        return guardId.equals(assignment.getGuard().getId())
                && !assignment.getShiftStart().isAfter(at)
                && (assignment.getShiftEnd() == null || !assignment.getShiftEnd().isBefore(at));
    }
}
