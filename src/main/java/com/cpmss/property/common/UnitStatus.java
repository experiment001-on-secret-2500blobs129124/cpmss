package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Unit occupancy status stored in {@code Unit_Status_History.unit_status}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_unit_status} constraint.
 */
public enum UnitStatus {
    /** Unit is available. */
    VACANT("Vacant"),
    /** Unit is currently occupied. */
    OCCUPIED("Occupied"),
    /** Unit is unavailable because maintenance is happening. */
    UNDER_MAINTENANCE("Under Maintenance"),
    /** Unit is reserved but not yet occupied. */
    RESERVED("Reserved");

    private final String label;

    UnitStatus(String label) {
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
     * Parses a required unit status label.
     *
     * @param label the unit status label
     * @return the matching status
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static UnitStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Unit status is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Unit status must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(UnitStatus::label).collect(Collectors.joining(", "));
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
