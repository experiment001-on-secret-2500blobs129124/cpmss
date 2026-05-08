package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Resident relationship stored in {@code Person_Resides_Under.household_relationship}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_household_relationship}
 * constraint.
 */
public enum HouseholdRelationship {
    /** Primary resident under the contract. */
    PRIMARY("Primary"),
    /** Spouse of the primary contract holder. */
    SPOUSE("Spouse"),
    /** Child resident. */
    CHILD("Child"),
    /** Sibling resident. */
    SIBLING("Sibling"),
    /** Guardian resident. */
    GUARDIAN("Guardian"),
    /** Co-tenant resident. */
    CO_TENANT("Co-tenant"),
    /** Guest resident. */
    GUEST("Guest");

    private final String label;

    HouseholdRelationship(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in {@code household_relationship}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required household relationship label.
     *
     * @param label the household relationship label
     * @return the matching relationship
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static HouseholdRelationship fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Household relationship is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Household relationship must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated household relationship labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(HouseholdRelationship::label)
                .collect(Collectors.joining(", "));
    }

    /**
     * Serializes this relationship as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
