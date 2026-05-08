package com.cpmss.communication.internalreport;

import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.BusinessException;

import java.util.Set;

/**
 * Business rules for {@link InternalReport} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see InternalReportService
 */
public class InternalReportRules {

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "Salary_Request", "Complaint", "Maintenance", "Leave_Request",
            "Incident", "Suggestion", "General"
    );

    /**
     * Validates that the assigned role is a valid system role.
     *
     * @param assignedToRole the role string to validate
     * @throws BusinessException if the role is not a valid SystemRole
     */
    public void validateAssignedToRole(String assignedToRole) {
        try {
            SystemRole.valueOf(assignedToRole);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid assigned role: '" + assignedToRole
                            + "'. Must be a valid SystemRole");
        }
    }

    /**
     * Validates that the report category is one of the known categories.
     *
     * @param reportCategory the category to validate
     * @throws BusinessException if the category is unknown
     */
    public void validateCategory(String reportCategory) {
        if (!VALID_CATEGORIES.contains(reportCategory)) {
            throw new BusinessException(
                    "Invalid report category: '" + reportCategory
                            + "'. Valid categories: " + VALID_CATEGORIES);
        }
    }
}
