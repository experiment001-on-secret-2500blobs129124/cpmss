package com.cpmss.property.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

/**
 * Service-level authorization rules for property and facility records.
 */
public class PropertyAccessRules {

    /**
     * Requires authority to mutate property records.
     *
     * @param user current authenticated user
     */
    public void requirePropertyAdministrator(CurrentUser user) {
        if (hasPropertyAuthority(user)) {
            return;
        }
        throw new ApiException(PropertyErrorCode.PROPERTY_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to read property records used by operations or finance.
     *
     * @param user current authenticated user
     */
    public void requirePropertyReader(CurrentUser user) {
        if (hasPropertyAuthority(user) || user.hasRole(SystemRole.ACCOUNTANT)) {
            return;
        }
        throw new ApiException(PropertyErrorCode.PROPERTY_RECORD_ACCESS_DENIED);
    }

    private boolean hasPropertyAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.FACILITY_OFFICER);
    }
}
