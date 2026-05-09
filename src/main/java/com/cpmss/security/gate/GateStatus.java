package com.cpmss.security.gate;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Operational state stored in {@code Gate.gate_status}.
 *
 * <p>The column is nullable, but any non-null value must match the Flyway V2
 * {@code chk_gate_status} constraint exactly.
 */
public enum GateStatus {
    /** Gate is available for normal operation. */
    ACTIVE("Active"),
    /** Gate is unavailable because maintenance is in progress. */
    UNDER_MAINTENANCE("Under Maintenance"),
    /** Gate is closed. */
    CLOSED("Closed");

    private final String label;

    GateStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact gate status label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a nullable gate status label.
     *
     * @param label the optional gate status label
     * @return the matching gate status, or {@code null} when absent
     * @throws ApiException if the label is blank or unsupported
     */
    public static GateStatus fromNullableLabel(String label) {
        if (label == null) {
            return null;
        }
        if (label.isBlank()) {
            throw new ApiException(SecurityErrorCode.GATE_STATUS_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_STATUS_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated gate status labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(GateStatus::label).collect(Collectors.joining(", "));
    }
}
