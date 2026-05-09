package com.cpmss.communication.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the communication bounded context.
 *
 * <p>Covers internal reports, report categories, priorities, and statuses.
 *
 * @see ErrorCode
 */
public enum CommunicationErrorCode implements ErrorCode {

    /** Report category is missing. */
    REPORT_CATEGORY_REQUIRED(422, "Report category is required"),

    /** Report priority is missing. */
    REPORT_PRIORITY_REQUIRED(422, "Report priority is required"),

    /** Report priority is invalid. */
    REPORT_PRIORITY_INVALID(422, "Report priority must be one of: Low, Normal, High, Urgent"),

    /** Report status is missing. */
    REPORT_STATUS_REQUIRED(422, "Report status is required"),

    /** Report status is invalid. */
    REPORT_STATUS_INVALID(422, "Report status must be one of: Open, In_Review, Resolved, Rejected"),

    /** Report not found. */
    REPORT_NOT_FOUND(404, "Report not found"),

    /** Target role is missing for the report. */
    REPORT_TARGET_ROLE_REQUIRED(422, "Target role is required"),

    /** Target role cannot process reports. */
    REPORT_TARGET_ROLE_INVALID(422, "Assigned role must be a valid internal-report receiver"),

    /** Report category is invalid. */
    REPORT_CATEGORY_INVALID(422, "Report category must be one of: Salary_Request, Transfer_Request, Complaint, Maintenance_Request, Security_Incident, Policy_Suggestion, General"),

    /** Report is already resolved. */
    REPORT_ALREADY_RESOLVED(409, "Report is already resolved"),

    /** User cannot access this internal report. */
    REPORT_ACCESS_DENIED(403, "Internal report access denied");

    private final int status;
    private final String defaultMessage;

    CommunicationErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() { return name(); }

    @Override
    public int status() { return status; }

    @Override
    public String defaultMessage() { return defaultMessage; }
}
