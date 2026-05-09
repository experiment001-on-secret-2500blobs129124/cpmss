package com.cpmss.platform.exception;

/**
 * Error codes shared across all bounded contexts.
 *
 * <p>Includes validation, authentication, authorization, resource lookup,
 * conflict, and shared platform value errors. Domain-specific codes live
 * in their own bounded-context enums (e.g. {@code FinanceErrorCode}).
 *
 * @see ErrorCode
 * @see ApiException
 */
public enum CommonErrorCode implements ErrorCode {

    // --- Validation ---

    /** DTO validation failed — one or more fields are invalid. */
    VALIDATION_FAILED(400, "Request validation failed"),

    /** One DTO field failed Bean Validation. */
    VALIDATION_FIELD_INVALID(400, "Field validation failed"),

    // --- Authentication ---

    /** Protected route called without a bearer token. */
    AUTHENTICATION_REQUIRED(401, "Authentication is required"),

    /** Bearer token is malformed, invalid, or unusable. */
    AUTHENTICATION_INVALID(401, "Authentication token is invalid"),

    /** Bearer token has expired. */
    AUTHENTICATION_EXPIRED(401, "Authentication token has expired"),

    // --- Authorization ---

    /** Authenticated user lacks route or resource permission. */
    ACCESS_DENIED(403, "Access denied"),

    /** Service expected an authenticated actor but none exists in the security context. */
    SECURITY_CONTEXT_MISSING(403, "No authenticated user in the security context"),

    // --- Resource Lookup ---

    /** Requested entity or aggregate was not found. */
    RESOURCE_NOT_FOUND(404, "Requested resource not found"),

    // --- Conflict ---

    /** Existing state conflicts with the requested change. */
    RESOURCE_CONFLICT(409, "Resource state conflict"),


    // --- Unexpected ---

    /** Unexpected server failure. */
    UNEXPECTED_ERROR(500, "An unexpected error occurred"),

    // --- Shared Platform Values ---

    /** Date range is missing or end is before start. */
    DATE_RANGE_INVALID(422, "Date range is missing or out of order"),

    /** Local time window is missing or end is before start. */
    TIME_WINDOW_INVALID(422, "Time window is missing or out of order"),

    /** Instant window is missing or end is before start. */
    INSTANT_WINDOW_INVALID(422, "Instant window is missing or out of order"),

    /** Year/month pair is invalid. */
    YEAR_MONTH_PERIOD_INVALID(422, "Year-month period is invalid"),

    /** Hours amount is missing. */
    HOURS_AMOUNT_REQUIRED(422, "Hours amount is missing"),

    /** Hours amount is not positive. */
    HOURS_AMOUNT_INVALID(422, "Hours amount must be positive"),

    /** Hour delta is missing. */
    HOUR_DELTA_REQUIRED(422, "Hour delta is missing");

    private final int status;
    private final String defaultMessage;

    CommonErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
