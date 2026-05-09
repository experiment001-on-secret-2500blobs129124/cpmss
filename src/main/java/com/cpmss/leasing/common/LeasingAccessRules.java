package com.cpmss.leasing.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

/**
 * Service-level authorization rules for leasing records.
 */
public class LeasingAccessRules {

    /**
     * Requires finance authority over leasing workflows.
     *
     * @param user current authenticated user
     */
    public void requireLeasingAuthority(CurrentUser user) {
        if (user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.ACCOUNTANT)) {
            return;
        }
        throw new ApiException(LeasingErrorCode.LEASING_RECORD_ACCESS_DENIED);
    }
}
