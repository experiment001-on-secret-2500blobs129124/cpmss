package com.cpmss.common;

import com.cpmss.exception.BusinessException;
import com.cpmss.exception.ConflictException;
import com.cpmss.exception.ForbiddenException;
import com.cpmss.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for all REST controllers.
 *
 * <p>Maps every custom exception to a structured JSON error response.
 * No try-catch blocks are needed in controllers or services.
 *
 * @see BusinessException
 * @see ResourceNotFoundException
 * @see ForbiddenException
 * @see ConflictException
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException} — resource not found in the database.
     *
     * @param ex the exception
     * @return 404 Not Found response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "Not Found", ex.getMessage(), Instant.now()));
    }

    /**
     * Handles {@link BusinessException} — a domain business rule was violated.
     *
     * @param ex the exception
     * @return 422 Unprocessable Entity response with error details
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Business rule violated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(422, "Unprocessable Entity", ex.getMessage(), Instant.now()));
    }

    /**
     * Handles {@link ForbiddenException} — authenticated user lacks permission.
     *
     * @param ex the exception
     * @return 403 Forbidden response with error details
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "Forbidden", ex.getMessage(), Instant.now()));
    }

    /**
     * Handles {@link ConflictException} — uniqueness constraint violated.
     *
     * @param ex the exception
     * @return 409 Conflict response with error details
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Conflict", ex.getMessage(), Instant.now()));
    }

    /**
     * Handles {@link MethodArgumentNotValidException} — {@code @Valid} format
     * validation failed on a request DTO.
     *
     * @param ex the exception containing per-field validation errors
     * @return 400 Bad Request response with a map of field → error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                ));
        log.warn("Validation failed: {}", fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(400, "Validation Failed", fields, Instant.now()));
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error",
                        "An unexpected error occurred", Instant.now()));
    }

    /**
     * Error response shape for single-message errors.
     *
     * @param status    HTTP status code
     * @param error     short error label
     * @param message   human-readable description
     * @param timestamp UTC timestamp of the error
     */
    record ErrorResponse(int status, String error, String message, Instant timestamp) {}

    /**
     * Error response shape for field-level validation failures.
     *
     * @param status    HTTP status code
     * @param error     short error label
     * @param fields    map of field name → validation message
     * @param timestamp UTC timestamp of the error
     */
    record ValidationErrorResponse(
            int status,
            String error,
            Map<String, String> fields,
            Instant timestamp
    ) {}
}
