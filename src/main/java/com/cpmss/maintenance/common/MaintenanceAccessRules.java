package com.cpmss.maintenance.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

/**
 * Service-level authorization rules for vendors and work orders.
 */
public class MaintenanceAccessRules {

    /**
     * Requires authority to mutate maintenance records.
     *
     * @param user current authenticated user
     */
    public void requireMaintenanceAdministrator(CurrentUser user) {
        if (hasMaintenanceAuthority(user)) {
            return;
        }
        throw new ApiException(MaintenanceErrorCode.MAINTENANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to read maintenance records.
     *
     * @param user current authenticated user
     */
    public void requireMaintenanceReader(CurrentUser user) {
        if (hasMaintenanceAuthority(user) || user.hasRole(SystemRole.ACCOUNTANT)) {
            return;
        }
        throw new ApiException(MaintenanceErrorCode.MAINTENANCE_RECORD_ACCESS_DENIED);
    }

    private boolean hasMaintenanceAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.FACILITY_OFFICER);
    }
}
