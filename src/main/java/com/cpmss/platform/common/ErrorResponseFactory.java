package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
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
        return new ErrorResponse(
                errorCode.status(),
                errorCode.code(),
                HttpStatus.valueOf(errorCode.status()).getReasonPhrase(),
                ex.getMessage(),
                ex.getFields(),
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
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
        return new ErrorResponse(
                errorCode.status(),
                errorCode.code(),
                HttpStatus.valueOf(errorCode.status()).getReasonPhrase(),
                errorCode.defaultMessage(),
                null,
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
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
        return new ErrorResponse(
                400,
                "VALIDATION_FAILED",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Request validation failed",
                fields,
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
    }

    /**
     * Builds an error response for legacy exceptions not yet migrated
     * to {@link ApiException}.
     *
     * <p>This method bridges the old string-only exceptions to the new
     * envelope shape. It will be removed after all throw sites are
     * migrated.
     *
     * @param status  the HTTP status code
     * @param code    the error code string
     * @param message the error message
     * @return the error response
     * @deprecated Remove after all throw sites use {@link ApiException}
     */
    @Deprecated
    public static ErrorResponse fromLegacyException(int status, String code, String message) {
        return new ErrorResponse(
                status,
                code,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message,
                null,
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
    }

    /**
     * Builds a generic 500 error response for unexpected failures.
     *
     * <p>The original exception message is never exposed to clients.
     *
     * @return the error response with status 500
     */
    public static ErrorResponse fromUnexpectedError() {
        return new ErrorResponse(
                500,
                "UNEXPECTED_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                null,
                MDC.get(MDC_REQUEST_ID),
                Instant.now()
        );
    }
}
