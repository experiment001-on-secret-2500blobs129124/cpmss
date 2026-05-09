package com.cpmss.communication.internalreport;

import com.cpmss.communication.common.CommunicationErrorCode;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.Set;

/**
 * Business rules for {@link InternalReport} operations.
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
}
