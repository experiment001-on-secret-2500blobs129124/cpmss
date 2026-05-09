package com.cpmss.communication.internalreport;

import com.cpmss.communication.common.CommunicationErrorCode;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.Set;
import java.util.UUID;

/**
 * Business and ownership rules for {@link InternalReport} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see InternalReportService
 */
public class InternalReportRules {

    private static final Set<SystemRole> REPORT_RECEIVER_ROLES = Set.of(
            SystemRole.ADMIN,
            SystemRole.GENERAL_MANAGER,
            SystemRole.HR_OFFICER,
            SystemRole.ACCOUNTANT,
            SystemRole.SECURITY_OFFICER,
            SystemRole.FACILITY_OFFICER,
            SystemRole.DEPARTMENT_MANAGER,
            SystemRole.SUPERVISOR
    );

    /**
     * Validates that the assigned role is a valid system role.
     *
     * @param assignedToRole the target role to validate
     * @throws ApiException if the role cannot process internal reports
     */
    public void validateAssignedToRole(SystemRole assignedToRole) {
        if (assignedToRole == null) {
            throw new ApiException(CommunicationErrorCode.REPORT_TARGET_ROLE_REQUIRED);
        }
        if (!REPORT_RECEIVER_ROLES.contains(assignedToRole)) {
            throw new ApiException(CommunicationErrorCode.REPORT_TARGET_ROLE_INVALID);
        }
    }

    /**
     * Checks whether the role can receive internal reports.
     *
     * @param role role to test
     * @return true when reports can be assigned to the role
     */
    public boolean canReceiveReports(SystemRole role) {
        return REPORT_RECEIVER_ROLES.contains(role);
    }

    /**
     * Validates that the report category is one of the known categories.
     *
     * @param reportCategory the category to validate
     * @throws ApiException if the category is unknown
     */
    public void validateCategory(ReportCategory reportCategory) {
        if (reportCategory == null) {
            throw new ApiException(CommunicationErrorCode.REPORT_CATEGORY_REQUIRED);
        }
    }

    /**
     * Validates that the current user can view the report.
     *
     * @param currentUser authenticated user
     * @param report      report being viewed
     * @throws ApiException if the user is not reporter, assignee, or business admin
     */
    public void validateCanView(CurrentUser currentUser, InternalReport report) {
        if (isBusinessAdmin(currentUser)
                || currentUser.hasRole(report.getAssignedToRole())
                || isReporter(currentUser, report)) {
            return;
        }
        throw new ApiException(CommunicationErrorCode.REPORT_ACCESS_DENIED);
    }

    /**
     * Validates that the current user can operate the assigned report queue.
     *
     * @param currentUser authenticated user
     * @param report      report being changed
     * @throws ApiException if the user is not assigned to process the report
     */
    public void validateCanProcess(CurrentUser currentUser, InternalReport report) {
        if (isBusinessAdmin(currentUser) || currentUser.hasRole(report.getAssignedToRole())) {
            return;
        }
        throw new ApiException(CommunicationErrorCode.REPORT_ACCESS_DENIED);
    }

    /**
     * Validates that the current user can request reports for a target role.
     *
     * @param currentUser authenticated user
     * @param assignedRole requested assigned role
     * @throws ApiException if the user does not own that role queue
     */
    public void validateCanAccessRoleQueue(CurrentUser currentUser, SystemRole assignedRole) {
        validateAssignedToRole(assignedRole);
        if (isBusinessAdmin(currentUser) || currentUser.hasRole(assignedRole)) {
            return;
        }
        throw new ApiException(CommunicationErrorCode.REPORT_ACCESS_DENIED);
    }

    /**
     * Validates that the current user can access a reporter's filed reports.
     *
     * @param currentUser authenticated user
     * @param reporterId  requested reporter person ID
     * @throws ApiException if the user does not own that reporter identity
     */
    public void validateCanAccessReporter(CurrentUser currentUser, UUID reporterId) {
        if (isBusinessAdmin(currentUser)
                || currentUser.requirePersonId("Viewing internal reports").equals(reporterId)) {
            return;
        }
        throw new ApiException(CommunicationErrorCode.REPORT_ACCESS_DENIED);
    }

    /**
     * Validates that a person field in a mutation belongs to the current user.
     *
     * @param currentUser authenticated user
     * @param personId    supplied person ID
     * @param action      action description used if the account is not person-linked
     * @throws ApiException if the supplied person is not the current person
     */
    public void validateCurrentPerson(CurrentUser currentUser, UUID personId, String action) {
        if (isBusinessAdmin(currentUser) || currentUser.requirePersonId(action).equals(personId)) {
            return;
        }
        throw new ApiException(CommunicationErrorCode.REPORT_ACCESS_DENIED);
    }

    /**
     * Checks whether the current user has unrestricted communication oversight.
     *
     * @param currentUser authenticated user
     * @return true for admin or general manager
     */
    public boolean isBusinessAdmin(CurrentUser currentUser) {
        return currentUser.hasRole(SystemRole.ADMIN)
                || currentUser.hasRole(SystemRole.GENERAL_MANAGER);
    }

    private boolean isReporter(CurrentUser currentUser, InternalReport report) {
        return currentUser.personId() != null
                && currentUser.personId().equals(report.getReporter().getId());
    }
}
