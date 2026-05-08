package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Contract target type stored in {@code Contract.contract_type}.
 *
 * <p>The type describes whether the contract is for a residential unit or a
 * commercial facility. The target FK mutual exclusion remains a service and
 * database rule.
 */
public enum ContractType {
    /** Residential unit contract. */
    RESIDENTIAL("Residential"),
    /** Commercial facility contract. */
    COMMERCIAL("Commercial");

    private final String label;

    ContractType(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in {@code contract_type}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required contract type label.
     *
     * @param label the contract type label
     * @return the matching type
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static ContractType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Contract type is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Contract type must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated contract type labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(ContractType::label)
                .collect(Collectors.joining(", "));
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
