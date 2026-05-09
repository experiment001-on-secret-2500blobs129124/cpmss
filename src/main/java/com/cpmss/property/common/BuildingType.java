package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Building structural category stored in {@code Building.building_type}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_building_type} constraint.
 */
public enum BuildingType {
    /** Building intended for residential use. */
    RESIDENTIAL("Residential"),
    /** Building intended for non-residential use. */
    NON_RESIDENTIAL("Non-Residential");

    private final String label;

    BuildingType(String label) {
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
     * Parses a required building type label.
     *
     * @param label the building type label
     * @return the matching type
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static BuildingType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Building type is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Building type must be one of: " + allowedLabels()));
    }

    /**
     * Parses an optional building type label.
     *
     * @param label the optional building type label
     * @return the matching type, or {@code null} when absent
     */
    public static BuildingType fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(BuildingType::label).collect(Collectors.joining(", "));
    }

    /**
     * Serializes this type as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
