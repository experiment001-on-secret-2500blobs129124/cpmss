package com.cpmss.hr.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the HR bounded context.
 *
 * <p>Covers salary amounts, compensation, and overtime/hours values.
 *
 * @see ErrorCode
 */
public enum HrErrorCode implements ErrorCode {

    /** Salary amount is missing. */
    SALARY_REQUIRED(422, "Salary amount is required"),

    /** Salary amount is negative. */
    SALARY_NEGATIVE(422, "Salary amount cannot be negative"),

    /** Overtime hours value is missing. */
    OVERTIME_HOURS_REQUIRED(422, "Overtime hours is required"),

    /** Overtime hours value is negative. */
    OVERTIME_HOURS_NEGATIVE(422, "Overtime hours cannot be negative"),

    /** Work hours value is missing. */
    WORK_HOURS_REQUIRED(422, "Work hours is required"),

    /** Work hours value is negative. */
    WORK_HOURS_NEGATIVE(422, "Work hours cannot be negative"),

    /** Salary amount must be positive. */
    SALARY_NOT_POSITIVE(422, "Salary amount must be positive"),

    /** Staff profile already exists. */
    STAFF_PROFILE_DUPLICATE(409, "Staff profile already exists"),

    /** No interview has passed. */
    NO_PASSING_INTERVIEW(422, "Cannot create hire agreement — at least one interview must have result 'Pass'"),

    /** Employment start date before application date. */
    START_DATE_BEFORE_APPLICATION(422, "Employment start date cannot be before application date"),

    /** Interview result is invalid. */
    INTERVIEW_RESULT_INVALID(422, "Interview result must be 'Pass', 'Fail', or 'Pending'"),

    /** Recruitment entity not found. */
    RECRUITMENT_NOT_FOUND(404, "Recruitment record not found"),

    /** Application not found. */
    APPLICATION_NOT_FOUND(404, "Application not found"),

    /** Staff position not found. */
    POSITION_NOT_FOUND(404, "Staff position not found"),

    /** Staff profile not found. */
    STAFF_PROFILE_NOT_FOUND(404, "Staff profile not found"),

    /** Person not found (HR context). */
    PERSON_NOT_FOUND(404, "Person not found"),

    /** Qualification not found. */
    QUALIFICATION_NOT_FOUND(404, "Qualification not found"),

    /** User cannot access this HR record. */
    HR_RECORD_ACCESS_DENIED(403, "HR record access denied");

    private final int status;
    private final String defaultMessage;

    HrErrorCode(int status, String defaultMessage) {
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
