package com.cpmss.identity.auth;

import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ForbiddenException;

import java.util.UUID;

/**
 * Business rules for {@link AppUser} account management operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 * Implements the cascading authority chain defined in REQUIREMENTS.md § 3.
 *
 * <p>Authority hierarchy (highest to lowest):
 * ADMIN → GENERAL_MANAGER → HR_OFFICER / ACCOUNTANT / SECURITY_OFFICER /
 * FACILITY_OFFICER → DEPARTMENT_MANAGER → SUPERVISOR → GATE_GUARD → STAFF
 *
 * @see AppUserService
 */
public class AppUserRules {

    /**
     * Validates that a user is not attempting to change their own role.
     *
     * <p>Self-promotion is never allowed — prevents privilege escalation.
     *
     * @param actorId  the UUID of the user performing the action
     * @param targetId the UUID of the user being modified
     * @throws ForbiddenException if actor and target are the same user
     */
    public void validateCannotChangeOwnRole(UUID actorId, UUID targetId) {
        if (actorId.equals(targetId)) {
            throw new ForbiddenException("Cannot change your own system role");
        }
    }

    /**
     * Validates that a user is not attempting to deactivate their own account.
     *
     * @param actorId  the UUID of the user performing the action
     * @param targetId the UUID of the user being deactivated
     * @throws ForbiddenException if actor and target are the same user
     */
    public void validateCannotDeactivateSelf(UUID actorId, UUID targetId) {
        if (actorId.equals(targetId)) {
            throw new ForbiddenException("Cannot deactivate your own account");
        }
    }

    /**
     * Validates that the actor has authority to assign or change to the target role.
     *
     * <p>Authority levels (per REQUIREMENTS.md § 3):
     * <ul>
     *   <li>ADMIN — can assign any role including GENERAL_MANAGER</li>
     *   <li>GENERAL_MANAGER — can assign up to officer level (HR, Accountant, etc.)</li>
     *   <li>HR_OFFICER — can assign up to DEPARTMENT_MANAGER</li>
     *   <li>DEPARTMENT_MANAGER — can only assign STAFF and GATE_GUARD</li>
     * </ul>
     *
     * @param actorRole  the system role of the user performing the action
     * @param targetRole the system role being assigned
     * @throws ForbiddenException if the actor lacks authority for this role assignment
     */
    public void validateAuthorityLevel(SystemRole actorRole, SystemRole targetRole) {
        if (actorRole == SystemRole.ADMIN) {
            return; // ADMIN can assign any role
        }

        int actorLevel = authorityLevel(actorRole);
        int targetLevel = authorityLevel(targetRole);

        if (targetLevel >= actorLevel) {
            throw new ForbiddenException(
                    "Cannot assign role " + targetRole + " — insufficient authority");
        }
    }

    /**
     * Validates that a DEPARTMENT_MANAGER can only create STAFF or GATE_GUARD accounts.
     *
     * <p>Department managers have the narrowest creation scope — they can
     * only onboard regular employees and gate guards within their department.
     *
     * @param actorRole  the system role of the user performing the action
     * @param targetRole the system role being assigned to the new account
     * @throws ForbiddenException if a DEPARTMENT_MANAGER tries to create
     *                            a role other than STAFF or GATE_GUARD
     */
    public void validateDeptManagerCanOnlyCreateStaffOrGuard(SystemRole actorRole,
                                                             SystemRole targetRole) {
        if (actorRole == SystemRole.DEPARTMENT_MANAGER
                && targetRole != SystemRole.STAFF
                && targetRole != SystemRole.GATE_GUARD) {
            throw new ForbiddenException(
                    "Department managers can only create STAFF and GATE_GUARD accounts");
        }
    }

    /**
     * Validates that an email is not already registered.
     *
     * @param email  the email to check
     * @param exists whether a user with this email already exists
     * @throws BusinessException if the email is already in use
     */
    public void validateEmailUnique(String email, boolean exists) {
        if (exists) {
            throw new BusinessException("Email '" + email + "' is already registered");
        }
    }

    // ── Private helpers ─────────────────────────────────────────────────

    /**
     * Returns a numeric authority level for role comparison.
     *
     * <p>Higher number = higher authority. ADMIN is handled separately
     * (bypasses all checks).
     */
    private int authorityLevel(SystemRole role) {
        return switch (role) {
            case ADMIN -> 100;
            case GENERAL_MANAGER -> 90;
            case HR_OFFICER -> 80;
            case ACCOUNTANT -> 80;
            case SECURITY_OFFICER -> 80;
            case FACILITY_OFFICER -> 80;
            case DEPARTMENT_MANAGER -> 70;
            case SUPERVISOR -> 60;
            case GATE_GUARD -> 50;
            case STAFF -> 40;
            case INVESTOR -> 30;
            case APPLICANT -> 20;
        };
    }
}
