package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
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
 * <p>Maps application exceptions raised after a request reaches Spring MVC
 * to the unified {@link ErrorResponse} envelope. Controllers and services
 * should throw {@link ApiException} with a stable
 * {@link com.cpmss.platform.exception.ErrorCode}; this handler converts the
 * code to the correct HTTP status and response body.
 *
 * <p>Spring Security 401/403 responses are produced before MVC exception
 * handling and are handled by the JSON security handlers.
 *
 * @see ApiException
 * @see ErrorResponseFactory
 * @see com.cpmss.platform.config.JsonAuthenticationEntryPoint
 * @see com.cpmss.platform.config.JsonAccessDeniedHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ApiException} — the standard application error path.
     *
     * <p>The HTTP status is determined by the {@code ErrorCode} carried
     * on the exception, not by the exception class.
     *
     * @param ex the API exception
     * @return the error response with the correct status
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse body = ErrorResponseFactory.fromApiException(ex);
        log.warn("error.api code={} status={} message={}",
                body.code(), body.status(), ex.getMessage());
        return ResponseEntity.status(body.status()).body(body);
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
        ErrorResponse body = ErrorResponseFactory.fromValidationFields(fields);
        log.warn("error.validation code={} status={} fields={}",
                body.code(), body.status(), fields.keySet());
        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Handles any unrecognised exception — last-resort fallback.
     *
     * @param ex the exception
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("error.unexpected", ex);
        ErrorResponse body = ErrorResponseFactory.fromUnexpectedError();
        return ResponseEntity.status(body.status()).body(body);
    }
}
