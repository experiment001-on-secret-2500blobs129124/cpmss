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

    /** Application already exists for applicant, position, and date. */
    APPLICATION_DUPLICATE(409, "Application already exists for applicant, position, and date"),

    /** Application CV file is missing. */
    APPLICATION_CV_FILE_REQUIRED(422, "Application CV file is required"),

    /** Application CV filename is missing. */
    APPLICATION_CV_FILENAME_REQUIRED(422, "Application CV filename is required"),

    /** Application CV content type is missing. */
    APPLICATION_CV_CONTENT_TYPE_REQUIRED(422, "Application CV content type is required"),

    /** Application CV size must be positive. */
    APPLICATION_CV_SIZE_INVALID(422, "Application CV size must be positive"),

    /** Application has no current CV. */
    APPLICATION_CV_NOT_FOUND(404, "Application CV not found"),

    /** Staff position not found. */
    POSITION_NOT_FOUND(404, "Staff position not found"),

    /** Staff profile not found. */
    STAFF_PROFILE_NOT_FOUND(404, "Staff profile not found"),

    /** Person not found (HR context). */
    PERSON_NOT_FOUND(404, "Person not found"),

    /** Qualification not found. */
    QUALIFICATION_NOT_FOUND(404, "Qualification not found"),

    /** Staff position assignment already exists. */
    STAFF_POSITION_ASSIGNMENT_DUPLICATE(409, "Staff position assignment already exists"),

    /** Position assignment effective date overlaps the current assignment. */
    STAFF_POSITION_ASSIGNMENT_OVERLAP(409, "Position assignment effective date overlaps the current assignment"),

    /** Position reassignment requires an authorizer. */
    STAFF_POSITION_AUTHORIZER_REQUIRED(422, "Position reassignment requires an authorizer"),

    /** Current staff position assignment was not found. */
    STAFF_POSITION_HISTORY_NOT_FOUND(404, "Current staff position assignment not found"),

    /** No current salary history exists for the staff member. */
    STAFF_SALARY_HISTORY_NOT_FOUND(404, "Current staff salary history not found"),

    /** Staff salary history already exists for this effective date. */
    STAFF_SALARY_HISTORY_DUPLICATE(409, "Staff salary history already exists for this effective date"),

    /** Staff salary exceeds the active position salary band. */
    STAFF_SALARY_EXCEEDS_POSITION_MAX(422, "Staff salary exceeds the active position salary band"),

    /** Position salary history was not found for the effective date. */
    POSITION_SALARY_HISTORY_NOT_FOUND(404, "Position salary history not found for this effective date"),

    /** Position salary history already exists for this effective date. */
    POSITION_SALARY_HISTORY_DUPLICATE(409, "Position salary history already exists for this effective date"),

    /** Shift attendance law row was not found. */
    SHIFT_ATTENDANCE_LAW_NOT_FOUND(404, "Shift attendance law not found"),

    /** Shift attendance law row already exists for this effective date. */
    SHIFT_ATTENDANCE_LAW_DUPLICATE(409, "Shift attendance law already exists for this effective date"),

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
