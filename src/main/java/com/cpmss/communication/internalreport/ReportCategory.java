package com.cpmss.communication.internalreport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Internal report category stored in {@code Internal_Report.report_category}.
 *
 * <p>Labels must match the Flyway V7 {@code chk_report_category}
 * constraint.
 */
public enum ReportCategory {
    /** Salary-related request. */
    SALARY_REQUEST("Salary_Request"),
    /** Transfer-related request. */
    TRANSFER_REQUEST("Transfer_Request"),
    /** Complaint report. */
    COMPLAINT("Complaint"),
    /** Maintenance-related request. */
    MAINTENANCE_REQUEST("Maintenance_Request"),
    /** Security incident report. */
    SECURITY_INCIDENT("Security_Incident"),
    /** Policy suggestion. */
    POLICY_SUGGESTION("Policy_Suggestion"),
    /** General report. */
    GENERAL("General");

    private final String label;

    ReportCategory(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required report category label.
     *
     * @param label the category label
     * @return the matching category
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static ReportCategory fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Report category is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Report category must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(ReportCategory::label).collect(Collectors.joining(", "));
    }

    /**
     * Serializes this category as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
