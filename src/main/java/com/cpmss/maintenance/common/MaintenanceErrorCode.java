package com.cpmss.maintenance.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the maintenance bounded context.
 *
 * <p>Covers work orders, service categories, schedules, and priorities.
 *
 * @see ErrorCode
 */
public enum MaintenanceErrorCode implements ErrorCode {

    /** Work order status is missing. */
    WORK_ORDER_STATUS_REQUIRED(422, "Work order status is required"),

    /** Work order priority is missing. */
    WORK_ORDER_PRIORITY_REQUIRED(422, "Work order priority is required"),

    /** Work order priority is invalid. */
    WORK_ORDER_PRIORITY_INVALID(422, "Work order priority must be one of: Low, Normal, High, Emergency"),

    /** Service category is missing. */
    SERVICE_CATEGORY_REQUIRED(422, "Service category is required"),

    /** Service category is invalid. */
    SERVICE_CATEGORY_INVALID(422, "Service category must be one of: Plumbing, Electrical, HVAC, Landscaping, Cleaning, Security"),

    /** Work order schedule dates are incomplete. */
    WORK_ORDER_SCHEDULE_INCOMPLETE(422, "Work order schedule start and end must be set together"),

    /** Work order schedule end is before start. */
    WORK_ORDER_SCHEDULE_INVALID(422, "Work order schedule end must be after start"),

    /** Work order cost must be positive. */
    WORK_ORDER_COST_NOT_POSITIVE(422, "Work order cost must be positive"),

    /** Work order status value is invalid. */
    WORK_ORDER_STATUS_INVALID(422, "Work order status must be one of: Pending, Assigned, In Progress, Completed, Paid, Cancelled"),

    /** Work order status transition is invalid. */
    WORK_ORDER_STATUS_TRANSITION_INVALID(422, "Invalid work order status transition"),

    /** Work order not found. */
    WORK_ORDER_NOT_FOUND(404, "Work order not found"),

    /** Company not found. */
    COMPANY_NOT_FOUND(404, "Company not found");

    private final int status;
    private final String defaultMessage;

    MaintenanceErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() { return name(); }

    @Override
    public int status() { return status; }

    @Override
    public String defaultMessage() { return defaultMessage; }
}
