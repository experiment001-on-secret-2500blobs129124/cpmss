package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Contract lifecycle status stored in {@code Contract.contract_status}.
 *
 * <p>Labels must remain compatible with the Flyway V2
 * {@code chk_contract_status} constraint.
 */
public enum ContractStatus {
    /** Contract is being drafted and is not yet active. */
    DRAFT("Draft"),
    /** Contract is currently active. */
    ACTIVE("Active"),
    /** Contract reached its natural end date. */
    EXPIRED("Expired"),
    /** Contract was closed before its natural end. */
    TERMINATED("Terminated"),
    /** Contract was superseded by a renewal contract. */
    RENEWED("Renewed");

    private final String label;

    ContractStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the database label for this contract status.
     *
     * @return the exact label stored in {@code contract_status}
     */
    public String label() {
        return label;
    }

    /**
     * Checks whether this status may transition to the requested status.
     *
     * @param next the requested next status
     * @return true when the transition is allowed
     */
    public boolean canTransitionTo(ContractStatus next) {
        if (this == next) {
            return true;
        }
        return switch (this) {
            case DRAFT -> next == ACTIVE || next == TERMINATED;
            case ACTIVE -> next == EXPIRED || next == TERMINATED || next == RENEWED;
            case EXPIRED, TERMINATED, RENEWED -> false;
        };
    }

    /**
     * Parses a required contract status label.
     *
     * @param label the contract status label
     * @return the matching status
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static ContractStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(LeasingErrorCode.CONTRACT_STATUS_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(LeasingErrorCode.CONTRACT_STATUS_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated contract status labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(ContractStatus::label)
                .collect(Collectors.joining(", "));
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
