package com.cpmss.maintenance.workorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Work-order lifecycle status stored in {@code Work_Order.job_status}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_job_status} constraint.
 */
public enum WorkOrderStatus {
    /** Work order has been filed but not assigned. */
    PENDING("Pending"),
    /** Work order has been assigned to a vendor or internal team. */
    ASSIGNED("Assigned"),
    /** Work is currently in progress. */
    IN_PROGRESS("In Progress"),
    /** Work has been completed. */
    COMPLETED("Completed"),
    /** Work order has been paid. */
    PAID("Paid"),
    /** Work order has been cancelled. */
    CANCELLED("Cancelled");

    private final String label;

    WorkOrderStatus(String label) {
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
     * Checks whether this status may transition to the requested status.
     *
     * @param next the requested next status
     * @return true when the transition is allowed
     */
    public boolean canTransitionTo(WorkOrderStatus next) {
        if (this == next) {
            return true;
        }
        return switch (this) {
            case PENDING -> next == ASSIGNED || next == IN_PROGRESS || next == CANCELLED;
            case ASSIGNED -> next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS -> next == COMPLETED || next == CANCELLED;
            case COMPLETED -> next == PAID;
            case PAID, CANCELLED -> false;
        };
    }

    /**
     * Parses a required status label.
     *
     * @param label the status label
     * @return the matching status
     * @throws BusinessException if the label is missing or unsupported
     */
    @JsonCreator
    public static WorkOrderStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Work order status is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Work order status must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(WorkOrderStatus::label).collect(Collectors.joining(", "));
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
