package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Unified error response envelope for all API errors.
 *
 * <p>Every error — validation, business rule, authentication, authorization,
 * conflict, not found, and unexpected — uses this single shape. The
 * {@code code} field is the stable client contract; {@code message} is
 * human-readable and may change between releases.
 *
 * <p>The {@code fields} map is omitted from JSON when {@code null},
 * keeping simple error responses compact.
 *
 * @param status    HTTP status code
 * @param code      stable upper-snake-case error code
 * @param error     short HTTP status label (e.g. "Unprocessable Entity")
 * @param message   human-readable error description
 * @param fields    field-level errors, or null when not field-specific
 * @param requestId the request ID from the {@code X-Request-Id} header
 * @param timestamp UTC timestamp of the error
 *
 * @see com.cpmss.platform.exception.ErrorCode
 * @see com.cpmss.platform.exception.ApiException
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String error,
        String message,
        Map<String, List<ApiException.FieldError>> fields,
        String requestId,
        Instant timestamp
) {}
