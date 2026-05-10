package com.cpmss.workforce.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the workforce bounded context.
 *
 * <p>Covers attendance, shifts, payroll, tasks, and assigned task rules.
 *
 * @see ErrorCode
 */
public enum WorkforceErrorCode implements ErrorCode {

    // --- Attendance Windows ---

    /** Check-in time is missing. */
    CHECKIN_REQUIRED(422, "Check-in time is required"),

    /** Check-out time is missing. */
    CHECKOUT_REQUIRED(422, "Check-out time is required"),

    /** Check-out time must be after check-in time. */
    CHECKOUT_BEFORE_CHECKIN(422, "Check-out time must be after check-in time"),

    /** Hour delta is missing. */
    HOUR_DELTA_REQUIRED(422, "Hour delta is required"),

    // --- Shift Windows ---

    /** Shift start time is missing. */
    SHIFT_START_REQUIRED(422, "Shift start time is required"),

    /** Shift end time is missing. */
    SHIFT_END_REQUIRED(422, "Shift end time is required"),

    /** Shift end time must be after start time. */
    SHIFT_END_BEFORE_START(422, "Shift end time must be after start time"),

    // --- Attendance Rules ---

    /** Times are required when staff is present. */
    ATTENDANCE_TIMES_REQUIRED(422, "Check-in and check-out times are required when staff is not absent"),

    /** Staff already has an attendance record for this date. */
    ATTENDANCE_DUPLICATE(409, "Attendance record already exists for this staff and date"),

    /** Attendance date does not fall within the assigned task period. */
    ATTENDANCE_DATE_OUTSIDE_TASK(422, "Attendance date is outside the assigned task period"),

    // --- Task/Shift Rules ---

    /** Task title already exists in the department. */
    TASK_DUPLICATE(409, "Task already exists in this department"),

    /** Shift attendance type name already exists. */
    SHIFT_TYPE_DUPLICATE(409, "Shift attendance type already exists"),

    /** Staff member already assigned to this task. */
    ASSIGNED_TASK_DUPLICATE(409, "Staff member is already assigned to this task"),

    /** Payroll period is required. */
    PAYROLL_PERIOD_REQUIRED(422, "Payroll period is required"),

    /** Payroll was already closed for the staff member and period. */
    PAYROLL_ALREADY_CLOSED(409, "Payroll is already closed for this staff member and period"),

    /** Payroll lookup requires a department or staff scope. */
    PAYROLL_SCOPE_REQUIRED(422, "Payroll lookup requires departmentId or staffId"),

    /** Payroll net amount exceeds the active salary cap. */
    PAYROLL_NET_EXCEEDS_SALARY_CAP(422, "Payroll net amount exceeds active salary cap"),

    /** Task not found. */
    TASK_NOT_FOUND(404, "Task not found"),

    /** Shift attendance type not found. */
    SHIFT_TYPE_NOT_FOUND(404, "Shift attendance type not found"),

    /** Assigned task not found. */
    ASSIGNED_TASK_NOT_FOUND(404, "Assigned task not found"),

    /** User cannot access this workforce record. */
    WORKFORCE_RECORD_ACCESS_DENIED(403, "Workforce record access denied");

    private final int status;
    private final String defaultMessage;

    WorkforceErrorCode(int status, String defaultMessage) {
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
