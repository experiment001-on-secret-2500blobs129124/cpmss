package com.cpmss.people.common;

import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Person gender vocabulary stored in {@code Person.gender}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_person_gender} constraint.
 */
public enum Gender {
    /** Male person gender. */
    MALE("Male"),
    /** Female person gender. */
    FEMALE("Female");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact gender label
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required gender label.
     *
     * @param label the gender label
     * @return the matching gender
     * @throws BusinessException if the label is missing or unsupported
     */
    public static Gender fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Gender is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Gender must be one of: " + allowedLabels()));
    }

    /**
     * Parses an optional gender label.
     *
     * @param label the optional gender label
     * @return the matching gender, or {@code null} when absent
     * @throws BusinessException if the label is blank or unsupported
     */
    public static Gender fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated gender labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(Gender::label).collect(Collectors.joining(", "));
    }
}
