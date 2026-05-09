package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Installment lifecycle status stored in {@code Installment.installment_status}.
 *
 * <p>The enum owns the finite vocabulary and the current transition table used
 * by {@code InstallmentRules}. Payment allocation workflows remain separate.
 */
public enum InstallmentStatus {
    /** Installment is not yet paid. */
    PENDING("Pending"),
    /** Installment has received some payment but is not fully paid. */
    PARTIALLY_PAID("Partially Paid"),
    /** Installment has been fully paid. */
    PAID("Paid"),
    /** Installment is past due. */
    OVERDUE("Overdue"),
    /** Installment was cancelled. */
    CANCELLED("Cancelled");

    private final String label;

    InstallmentStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in {@code installment_status}
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
    public boolean canTransitionTo(InstallmentStatus next) {
        if (this == next) {
            return true;
        }
        return switch (this) {
            case PENDING -> next == PARTIALLY_PAID || next == PAID
                    || next == OVERDUE || next == CANCELLED;
            case PARTIALLY_PAID -> next == PAID || next == OVERDUE;
            case OVERDUE -> next == PARTIALLY_PAID || next == PAID || next == CANCELLED;
            case PAID, CANCELLED -> false;
        };
    }

    /**
     * Parses a required installment status label.
     *
     * @param label the installment status label
     * @return the matching status
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static InstallmentStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_STATUS_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(LeasingErrorCode.INSTALLMENT_STATUS_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated installment status labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(InstallmentStatus::label)
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
