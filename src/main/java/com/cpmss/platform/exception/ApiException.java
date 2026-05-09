package com.cpmss.platform.exception;

import java.util.List;
import java.util.Map;

/**
 * Single exception class for all API error responses.
 *
 * <p>Consolidates status-specific application failures behind one
 * exception type. The HTTP status, stable error code, and default message
 * are all carried by the {@link ErrorCode}, making the exception a delivery
 * mechanism rather than a status decision point.
 *
 * <p>Throw sites use one of three patterns:
 * <pre>{@code
 * // Default message from the ErrorCode
 * throw new ApiException(FinanceErrorCode.MONEY_AMOUNT_REQUIRED);
 *
 * // Context-specific message override
 * throw new ApiException(FinanceErrorCode.MONEY_AMOUNT_REQUIRED,
 *     "Installment payment amount is required");
 *
 * // With field-level errors
 * throw new ApiException(CommonErrorCode.VALIDATION_FAILED, fields);
 * }</pre>
 *
 * @see ErrorCode
 * @see com.cpmss.platform.common.ErrorResponse
 * @see com.cpmss.platform.common.GlobalExceptionHandler
 */
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, List<FieldError>> fields;

    /**
     * Constructs an exception using the error code's default message.
     *
     * @param errorCode the error code defining status, code, and message
     */
    public ApiException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
        this.fields = null;
    }

    /**
     * Constructs an exception with a context-specific message override.
     *
     * <p>Use this when the default message from the error code is too
     * generic and extra context helps the caller fix the problem.
     *
     * @param errorCode the error code defining status and code
     * @param message   context-specific human-readable message
     */
    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fields = null;
    }

    /**
     * Constructs an exception with field-level errors.
     *
     * <p>Use this when a service can report multiple specific rule
     * failures at once, each tied to a request field path.
     *
     * @param errorCode the error code defining status and code
     * @param fields    map of field path to its validation errors
     */
    public ApiException(ErrorCode errorCode, Map<String, List<FieldError>> fields) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
        this.fields = fields;
    }

    /**
     * Returns the error code that defines this exception's HTTP status
     * and stable client-facing code.
     *
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the field-level errors, or {@code null} if this exception
     * is not field-specific.
     *
     * @return field errors map, or null
     */
    public Map<String, List<FieldError>> getFields() {
        return fields;
    }

    /**
     * A single field-level error within a business rule violation.
     *
     * @param code    stable error code for this specific field failure
     * @param message human-readable description of the field error
     */
    public record FieldError(String code, String message) {}
}
