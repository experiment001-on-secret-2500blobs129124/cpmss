package com.cpmss.performance.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the performance bounded context.
 *
 * <p>Covers KPI scores, percentage rates, performance ratings,
 * KPI policy, staff KPI records, monthly summaries, and reviews.
 *
 * @see ErrorCode
 */
public enum PerformanceErrorCode implements ErrorCode {

    /** KPI score is missing. */
    KPI_SCORE_REQUIRED(422, "KPI score is required"),

    /** KPI score is negative. */
    KPI_SCORE_NEGATIVE(422, "KPI score cannot be negative"),

    /** KPI score range bounds are missing. */
    KPI_SCORE_RANGE_REQUIRED(422, "KPI score range bounds are required"),

    /** KPI score range max is not greater than min. */
    KPI_SCORE_RANGE_INVALID(422, "KPI score range max must be greater than min"),

    /** Percentage rate is missing. */
    PERCENTAGE_RATE_REQUIRED(422, "Percentage rate is required"),

    /** Percentage rate is negative. */
    PERCENTAGE_RATE_NEGATIVE(422, "Percentage rate cannot be negative"),

    /** Performance rating is missing. */
    PERFORMANCE_RATING_REQUIRED(422, "Performance rating is required"),

    /** Performance rating label is unsupported. */
    PERFORMANCE_RATING_INVALID(422, "Performance rating is not allowed"),

    /** KPI policy already exists for this department. */
    KPI_POLICY_DUPLICATE(409, "KPI policy already exists for this department and KPI type"),

    /** Staff KPI record already exists for this date. */
    KPI_RECORD_DUPLICATE(409, "KPI record already exists for this staff, date, and KPI policy"),

    /** Monthly summary already exists for this period. */
    KPI_SUMMARY_DUPLICATE(409, "Monthly KPI summary already exists for this staff and period"),

    /** Self-review is not allowed. */
    SELF_REVIEW_FORBIDDEN(403, "A staff member cannot review themselves"),

    /** Performance review already exists for this period. */
    REVIEW_DUPLICATE(409, "Performance review already exists for this staff and period"),

    /** Poor rating with promotion or raise. */
    POOR_RATING_INCONSISTENT(422, "A 'Poor' rating cannot result in a promotion or raise"),

    /** Review outcome flags cannot be changed without outcome details. */
    REVIEW_OUTCOME_IMMUTABLE(409, "Performance review outcomes cannot be changed after creation"),

    /** KPI policy is not active for the record date. */
    KPI_POLICY_NOT_ACTIVE(422, "KPI policy is not yet active for this record date"),

    /** KPI tier overlaps with an existing tier. */
    KPI_TIER_OVERLAP(409, "KPI tier overlaps with an existing tier in this department"),

    /** Monthly KPI close requires a closer. */
    KPI_CLOSER_REQUIRED(422, "Monthly KPI close requires a manager or HR officer"),

    /** KPI policy not found. */
    KPI_POLICY_NOT_FOUND(404, "KPI policy not found"),

    /** KPI monthly summary already exists. */
    KPI_MONTH_ALREADY_CLOSED(409, "KPI month is already closed for this staff member"),

    /** Promotion requires a target position. */
    PROMOTION_POSITION_REQUIRED(422, "Promotion requires a target position"),

    /** Raise requires salary values. */
    RAISE_SALARY_REQUIRED(422, "Raise requires salary values"),

    /** Performance review not found. */
    REVIEW_NOT_FOUND(404, "Performance review not found"),

    /** User cannot access this performance record. */
    PERFORMANCE_RECORD_ACCESS_DENIED(403, "Performance record access denied");

    private final int status;
    private final String defaultMessage;

    PerformanceErrorCode(int status, String defaultMessage) {
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
