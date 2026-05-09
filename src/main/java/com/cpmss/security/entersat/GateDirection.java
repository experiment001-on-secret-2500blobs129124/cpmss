package com.cpmss.security.entersat;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Direction of travel stored in {@code Enters_At.direction}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_gate_direction} constraint.
 */
public enum GateDirection {
    /** Person or vehicle entered the compound. */
    IN("In"),
    /** Person or vehicle exited the compound. */
    OUT("Out");

    private final String label;

    GateDirection(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact direction label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a gate direction label.
     *
     * @param label the gate direction label
     * @return the matching gate direction
     * @throws ApiException if the label is missing or unsupported
     */
    public static GateDirection fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(SecurityErrorCode.GATE_DIRECTION_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_DIRECTION_REQUIRED));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated gate direction labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(GateDirection::label).collect(Collectors.joining(", "));
    }
}
