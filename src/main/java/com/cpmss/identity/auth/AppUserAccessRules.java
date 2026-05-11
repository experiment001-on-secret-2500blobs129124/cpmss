package com.cpmss.identity.auth;

import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for AppUser account records.
 */
public class AppUserAccessRules {

    /**
     * Requires authority to create login accounts.
     *
     * <p>Department managers can create lowest-level STAFF and GATE_GUARD
     * accounts. Accountants can provision investor accounts after investment
     * onboarding. In both cases, {@link AppUserRules} validates the exact target role.
     *
     * @param user current authenticated user
     */
    public void requireAccountCreator(CurrentUser user) {
        if (isAccountManager(user)
                || user.hasRole(SystemRole.DEPARTMENT_MANAGER)
                || user.hasRole(SystemRole.ACCOUNTANT)) {
            return;
        }
        throw new ApiException(IdentityErrorCode.IDENTITY_RECORD_ACCESS_DENIED);
    }

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
