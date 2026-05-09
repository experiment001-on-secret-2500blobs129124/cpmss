package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ConflictException;
import com.cpmss.platform.exception.ForbiddenException;
import com.cpmss.platform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for all REST controllers.
 *
 * <p>Maps every exception to a unified {@link ErrorResponse} envelope.
 * No try-catch blocks are needed in controllers or services.
 *
 * <p>The primary handler is for {@link ApiException}, which carries an
 * {@link com.cpmss.platform.exception.ErrorCode} that determines the
 * HTTP status, stable code, and default message. Legacy exception
 * handlers remain until all throw sites are migrated.
 *
 * @see ApiException
 * @see ErrorResponseFactory
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ApiException} — the standard error path.
     *
     * <p>The HTTP status is determined by the {@code ErrorCode} carried
     * on the exception, not by the exception class.
     *
     * @param ex the API exception
     * @return the error response with the correct status
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        log.warn("error.api code={} message={}", ex.getErrorCode().code(), ex.getMessage());
        ErrorResponse body = ErrorResponseFactory.fromApiException(ex);
        return ResponseEntity.status(body.status()).body(body);
    }

    // ---------------------------------------------------------------
    // Legacy exception handlers — kept until all throw sites migrate
    // to ApiException. Each maps to the same ErrorResponse envelope.
    // ---------------------------------------------------------------

    /**
     * Handles {@link ResourceNotFoundException} — resource not found.
     *
     * @param ex the exception
     * @return 404 Not Found response
     * @deprecated Migrate throw sites to {@code ApiException(CommonErrorCode.RESOURCE_NOT_FOUND)}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse body = ErrorResponseFactory.fromLegacyException(
                404, "RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(404).body(body);
    }

    /**
     * Handles {@link BusinessException} — a domain business rule was violated.
     *
     * @param ex the exception
     * @return 422 Unprocessable Entity response
     * @deprecated Migrate throw sites to {@code ApiException(SomeErrorCode.SPECIFIC_CODE)}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Business rule violated: {}", ex.getMessage());
        ErrorResponse body = ErrorResponseFactory.fromLegacyException(
                422, "BUSINESS_RULE_VIOLATION", ex.getMessage());
        return ResponseEntity.status(422).body(body);
    }

    /**
     * Handles {@link ForbiddenException} — authenticated user lacks permission.
     *
     * @param ex the exception
     * @return 403 Forbidden response
     * @deprecated Migrate throw sites to {@code ApiException(CommonErrorCode.ACCESS_DENIED)}
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        ErrorResponse body = ErrorResponseFactory.fromLegacyException(
                403, "ACCESS_DENIED", ex.getMessage());
        return ResponseEntity.status(403).body(body);
    }

    /**
     * Handles {@link ConflictException} — uniqueness constraint violated.
     *
     * @param ex the exception
     * @return 409 Conflict response
     * @deprecated Migrate throw sites to {@code ApiException(SomeErrorCode.SPECIFIC_CONFLICT)}
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        ErrorResponse body = ErrorResponseFactory.fromLegacyException(
                409, "RESOURCE_CONFLICT", ex.getMessage());
        return ResponseEntity.status(409).body(body);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} — {@code @Valid} format
     * validation failed on a request DTO.
     *
     * @param ex the exception containing per-field validation errors
     * @return 400 Bad Request response with field-level errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<ApiException.FieldError>> fields = ex.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                e -> new ApiException.FieldError(
                                        "VALIDATION_FIELD_INVALID",
                                        e.getDefaultMessage() != null
                                                ? e.getDefaultMessage()
                                                : "Invalid value"),
                                Collectors.toList()
                        )
                ));
        log.warn("Validation failed: {}", fields.keySet());
        ErrorResponse body = ErrorResponseFactory.fromValidationFields(fields);
        return ResponseEntity.status(400).body(body);
    }

    /**
     * Handles any unrecognised exception — last-resort fallback.
     *
     * @param ex the exception
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorResponse body = ErrorResponseFactory.fromUnexpectedError();
        return ResponseEntity.status(500).body(body);
    }
}
