package com.cpmss.communication.internalreport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Internal report lifecycle status stored in {@code Internal_Report.report_status}.
 *
 * <p>Labels must match the Flyway V7 {@code chk_report_status}
 * constraint.
 */
public enum ReportStatus {
    /** Report is open and not yet reviewed. */
    OPEN("Open"),
    /** Report is actively being reviewed. */
    IN_REVIEW("In_Review"),
    /** Report has been resolved. */
    RESOLVED("Resolved"),
    /** Report has been rejected. */
    REJECTED("Rejected");

    private final String label;

    ReportStatus(String label) {
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
     * Parses a required report status label.
     *
     * @param label the status label
     * @return the matching status
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static ReportStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Report status is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Report status must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(ReportStatus::label).collect(Collectors.joining(", "));
    }

    /**
     * Serializes this status as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
