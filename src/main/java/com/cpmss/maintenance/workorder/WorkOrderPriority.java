package com.cpmss.maintenance.workorder;

import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Work-order urgency level stored in {@code Work_Order.priority}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_priority} constraint.
 */
public enum WorkOrderPriority {
    /** Low urgency. */
    LOW("Low"),
    /** Normal urgency. */
    NORMAL("Normal"),
    /** High urgency. */
    HIGH("High"),
    /** Emergency urgency. */
    EMERGENCY("Emergency");

    private final String label;

    WorkOrderPriority(String label) {
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
     * Parses a required priority label.
     *
     * @param label the priority label
     * @return the matching priority
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static WorkOrderPriority fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(MaintenanceErrorCode.WORK_ORDER_PRIORITY_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.WORK_ORDER_PRIORITY_INVALID));
    }

    /**
     * Parses an optional priority label.
     *
     * @param label the optional priority label
     * @return the matching priority, or {@code null} when absent
     */
    public static WorkOrderPriority fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(WorkOrderPriority::label).collect(Collectors.joining(", "));
    }

    /**
     * Serializes this priority as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
