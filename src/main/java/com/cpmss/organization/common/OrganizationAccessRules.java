package com.cpmss.organization.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for organization records.
 */
public class OrganizationAccessRules {

    /**
     * Requires HR authority over department setup and manager assignments.
     *
     * @param user current authenticated user
     */
    public void requireOrganizationAdministrator(CurrentUser user) {
        if (hasOrganizationAuthority(user)) {
            return;
        }
        throw new ApiException(OrganizationErrorCode.ORGANIZATION_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows broad organization readers or an assigned department manager.
     *
     * @param user         current authenticated user
     * @param departmentId department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanViewDepartment(CurrentUser user, UUID departmentId,
                                         DepartmentScopeService scopeService) {
        if (hasOrganizationAuthority(user) || scopeService.managesDepartment(user, departmentId)) {
            return;
        }
        throw new ApiException(OrganizationErrorCode.ORGANIZATION_RECORD_ACCESS_DENIED);
    }

    private boolean hasOrganizationAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }
}
