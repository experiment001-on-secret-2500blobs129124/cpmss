package com.cpmss.communication.internalreport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Internal report priority stored in {@code Internal_Report.priority}.
 *
 * <p>Labels must match the Flyway V7 {@code chk_report_priority}
 * constraint.
 */
public enum ReportPriority {
    /** Low priority report. */
    LOW("Low"),
    /** Normal priority report. */
    NORMAL("Normal"),
    /** High priority report. */
    HIGH("High"),
    /** Urgent priority report. */
    URGENT("Urgent");

    private final String label;

    ReportPriority(String label) {
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
     * Parses a required priority label.
     *
     * @param label the priority label
     * @return the matching priority
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static ReportPriority fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Report priority is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Report priority must be one of: " + allowedLabels()));
    }

    /**
     * Parses an optional priority label.
     *
     * @param label the optional priority label
     * @return the matching priority, or {@code null} when absent
     */
    public static ReportPriority fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(ReportPriority::label).collect(Collectors.joining(", "));
    }

    /**
     * Serializes this priority as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
