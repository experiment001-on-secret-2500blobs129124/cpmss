package com.cpmss.workforce.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.taskmonthlysalary.TaskMonthlySalary;

import java.util.UUID;

/**
 * Service-level authorization rules for attendance, tasks, and payroll scope.
 */
public class WorkforceAccessRules {

    /**
     * Requires authority to manage a department operation.
     *
     * @param user         current authenticated user
     * @param departmentId department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanManageDepartment(CurrentUser user, UUID departmentId,
                                           DepartmentScopeService scopeService) {
        if (scopeService.isBusinessAdmin(user)
                || scopeService.managesDepartment(user, departmentId)) {
            return;
        }
        throw new ApiException(WorkforceErrorCode.WORKFORCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to read workforce records for one staff member.
     *
     * @param user         current authenticated user
     * @param staffId      staff person UUID
     * @param departmentId department UUID for the record, if already known
     * @param scopeService department ownership resolver
     */
    public void requireCanViewStaffWorkforce(CurrentUser user, UUID staffId,
                                             UUID departmentId,
                                             DepartmentScopeService scopeService) {
        if (scopeService.isBusinessAdmin(user)
                || user.hasRole(SystemRole.HR_OFFICER)
                || isOwnPerson(user, staffId)
                || scopeService.supervises(user, staffId)
                || canManageResolvedDepartment(user, staffId, departmentId, scopeService)) {
            return;
        }
        throw new ApiException(WorkforceErrorCode.WORKFORCE_RECORD_ACCESS_DENIED);
    }


    /**
     * Requires authority to assign a staff member to a task in a department.
     *
     * @param user         current authenticated user
     * @param staffId      staff person UUID
     * @param departmentId task department UUID
     * @param scopeService department ownership resolver
     */
    public void requireCanAssignStaffToDepartment(CurrentUser user, UUID staffId,
                                                  UUID departmentId,
                                                  DepartmentScopeService scopeService) {
        if (scopeService.isBusinessAdmin(user)) {
            return;
        }
        if (scopeService.managesDepartment(user, departmentId)
                && scopeService.staffBelongsToDepartment(staffId, departmentId)) {
            return;
        }
        throw new ApiException(WorkforceErrorCode.WORKFORCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires finance authority for payroll money workflows.
     *
     * @param user current authenticated user
     */
    public void requirePayrollFinance(CurrentUser user) {
        if (hasPayrollFinance(user)) {
            return;
        }
        throw new ApiException(WorkforceErrorCode.WORKFORCE_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows payroll finance or the linked staff member to read a payroll row.
     *
     * @param user    current authenticated user
     * @param payroll payroll row being read
     */
    public void requireCanViewPayrollRecord(CurrentUser user, TaskMonthlySalary payroll) {
        if (hasPayrollFinance(user) || isOwnPerson(user, payroll.getStaff().getId())) {
            return;
        }
        throw new ApiException(WorkforceErrorCode.WORKFORCE_RECORD_ACCESS_DENIED);
    }

    private boolean hasPayrollFinance(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.ACCOUNTANT);
    }

    private boolean canManageResolvedDepartment(CurrentUser user, UUID staffId,
                                                UUID departmentId,
                                                DepartmentScopeService scopeService) {
        UUID resolvedDepartmentId = departmentId;
        if (resolvedDepartmentId == null) {
            resolvedDepartmentId = scopeService.activeDepartmentForStaff(staffId).orElse(null);
        }
        return resolvedDepartmentId != null
                && scopeService.managesDepartment(user, resolvedDepartmentId);
    }

    private boolean isOwnPerson(CurrentUser user, UUID personId) {
        return user.personId() != null && user.personId().equals(personId);
    }
}
