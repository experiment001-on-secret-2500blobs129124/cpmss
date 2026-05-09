package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import com.cpmss.platform.exception.ErrorCode;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Factory for consistent {@link ErrorResponse} construction.
 *
 * <p>Every error response in the system — from {@link ApiException},
 * validation failures, or security handlers — is built through this
 * factory to guarantee the standard envelope shape.
 *
 * <p>The request ID is read from the SLF4J MDC {@code requestId} key,
 * which is set by the request ID filter. If no request ID is available
 * (e.g. the error occurs before the filter runs), the field is null.
 *
 * @see ErrorResponse
 * @see com.cpmss.platform.config.RequestIdFilter
 */
public final class ErrorResponseFactory {

    /** MDC key used to store and retrieve the current request ID. */
    public static final String MDC_REQUEST_ID = "requestId";

    private ErrorResponseFactory() {}

    /**
     * Builds an error response from an {@link ApiException}.
     *
     * @param ex the API exception
     * @return the error response with code, status, message, and optional fields
     */
    public static ErrorResponse fromApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return build(errorCode, ex.getMessage(), ex.getFields());
    }

    /**
     * Builds an error response from an {@link ErrorCode} using its default message.
     *
     * <p>Used by security handlers (401/403) that construct responses outside
     * the controller exception handler.
     *
     * @param errorCode the error code
     * @return the error response
     */
    public static ErrorResponse fromErrorCode(ErrorCode errorCode) {
        return build(errorCode, errorCode.defaultMessage(), null);
    }

    /**
     * Builds a validation error response with field-level details.
     *
     * <p>Used by the exception handler for {@code MethodArgumentNotValidException}.
     *
     * @param fields map of field path to its validation error list
     * @return the error response with status 400
     */
    public static ErrorResponse fromValidationFields(
            Map<String, List<ApiException.FieldError>> fields) {
        return build(CommonErrorCode.VALIDATION_FAILED,
                CommonErrorCode.VALIDATION_FAILED.defaultMessage(), fields);
    }

    /**
     * Builds a generic 500 error response for unexpected failures.
     *
     * <p>The original exception message is never exposed to clients.
     *
     * @return the error response with status 500
     */
    public static ErrorResponse fromUnexpectedError() {
        return fromErrorCode(CommonErrorCode.UNEXPECTED_ERROR);
    }

    private static ErrorResponse build(ErrorCode errorCode,
                                       String message,
                                       Map<String, List<ApiException.FieldError>> fields) {
        return new ErrorResponse(
                errorCode.status(),
                errorCode.code(),
                HttpStatus.valueOf(errorCode.status()).getReasonPhrase(),
                message,
                fields,
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
    }
}
