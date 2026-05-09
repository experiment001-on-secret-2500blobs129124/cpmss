package com.cpmss.identity.auth;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;

import java.util.UUID;

/**
 * Authenticated software user resolved from the security context.
 *
 * <p>Services use this value when authorization depends on the current
 * account, linked person, or system role. It keeps resource-ownership checks
 * away from raw {@code SecurityContextHolder} access.
 *
 * @param userId     the AppUser UUID
 * @param personId   linked Person UUID, or {@code null} for account-only users
 * @param systemRole the user's software role
 * @param email      the normalized login email
 */
public record CurrentUser(
        UUID userId,
        UUID personId,
        SystemRole systemRole,
        String email
) {

    /**
     * Checks whether the current user has the given system role.
     *
     * @param role the role to compare
     * @return true when the current user has the role
     */
    public boolean hasRole(SystemRole role) {
        return systemRole == role;
    }

    /**
     * Returns the linked person id or rejects account-only access.
     *
     * <p>Some actions, such as gate guard entry logging, must be tied to a
     * real person and cannot be performed by an unlinked login account.
     *
     * @param actionDescription the business action that needs a person link
     * @return the linked person UUID
     * @throws ApiException if this login account has no linked person
     */
    public UUID requirePersonId(String actionDescription) {
        if (personId == null) {
            throw new ApiException(
                    CommonErrorCode.ACCESS_DENIED,
                    actionDescription + " requires a linked person");
        }
        return personId;
    }
}
