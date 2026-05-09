package com.cpmss.performance.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for KPI records and performance reviews.
 */
public class PerformanceAccessRules {

    /**
     * Requires HR authority for KPI policies and cross-department review data.
     *
     * @param user current authenticated user
     */
    public void requireHrOrBusinessAdmin(CurrentUser user) {
        if (isHrOrBusinessAdmin(user)) {
            return;
        }
        throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to manage performance data for a department.
     *
     * @param user         current authenticated user
     * @param departmentId department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanManageDepartment(CurrentUser user, UUID departmentId,
                                           DepartmentScopeService scopeService) {
        if (isHrOrBusinessAdmin(user) || scopeService.managesDepartment(user, departmentId)) {
            return;
        }
        throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to read one staff member's performance data.
     *
     * @param user         current authenticated user
     * @param staffId      staff person UUID
     * @param departmentId department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanViewStaffPerformance(CurrentUser user, UUID staffId,
                                               UUID departmentId,
                                               DepartmentScopeService scopeService) {
        if (isHrOrBusinessAdmin(user)
                || isOwnPerson(user, staffId)
                || scopeService.supervises(user, staffId)
                || scopeService.managesDepartment(user, departmentId)) {
            return;
        }
        throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires that a reviewer is allowed to create a review in the department.
     *
     * @param user         current authenticated user
     * @param reviewerId   reviewer person UUID on the record
     * @param departmentId department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanCreateReview(CurrentUser user, UUID reviewerId,
                                       UUID departmentId,
                                       DepartmentScopeService scopeService) {
        if (isHrOrBusinessAdmin(user)) {
            return;
        }
        if (user.hasRole(SystemRole.DEPARTMENT_MANAGER)
                && isOwnPerson(user, reviewerId)
                && scopeService.managesDepartment(user, departmentId)) {
            return;
        }
        throw new ApiException(PerformanceErrorCode.PERFORMANCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Checks whether the user has HR or business administrator authority.
     *
     * @param user current authenticated user
     * @return true for ADMIN, GENERAL_MANAGER, or HR_OFFICER
     */
    public boolean isHrOrBusinessAdmin(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }

    private boolean isOwnPerson(CurrentUser user, UUID personId) {
        return user.personId() != null && user.personId().equals(personId);
    }
}
