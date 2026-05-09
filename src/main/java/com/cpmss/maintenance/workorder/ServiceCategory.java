package com.cpmss.maintenance.workorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Work-order service category stored in {@code Work_Order.service_category}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_service_category}
 * constraint.
 */
public enum ServiceCategory {
    /** Plumbing service category. */
    PLUMBING("Plumbing"),
    /** Electrical service category. */
    ELECTRICAL("Electrical"),
    /** Heating, ventilation, and air-conditioning service category. */
    HVAC("HVAC"),
    /** Landscaping service category. */
    LANDSCAPING("Landscaping"),
    /** Cleaning service category. */
    CLEANING("Cleaning"),
    /** Security service category. */
    SECURITY("Security");

    private final String label;

    ServiceCategory(String label) {
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
     * Parses a required service category label.
     *
     * @param label the service category label
     * @return the matching category
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static ServiceCategory fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Service category is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Service category must be one of: " + allowedLabels()));
    }

    /**
     * Parses an optional service category label.
     *
     * @param label the optional category label
     * @return the matching category, or {@code null} when absent
     */
    public static ServiceCategory fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(ServiceCategory::label).collect(Collectors.joining(", "));
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
