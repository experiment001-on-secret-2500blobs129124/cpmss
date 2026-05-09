package com.cpmss.identity.auth;

import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for AppUser account records.
 */
public class AppUserAccessRules {

    /**
     * Requires authority to read account management records.
     *
     * @param user current authenticated user
     */
    public void requireAccountManager(CurrentUser user) {
        if (isAccountManager(user)) {
            return;
        }
        throw new ApiException(IdentityErrorCode.IDENTITY_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows account managers or the logged-in account's own row.
     *
     * @param user   current authenticated user
     * @param userId account UUID being read
     */
    public void requireCanViewAccount(CurrentUser user, UUID userId) {
        if (isAccountManager(user) || user.userId().equals(userId)) {
            return;
        }
        throw new ApiException(IdentityErrorCode.IDENTITY_RECORD_ACCESS_DENIED);
    }

    private boolean isAccountManager(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }
}
