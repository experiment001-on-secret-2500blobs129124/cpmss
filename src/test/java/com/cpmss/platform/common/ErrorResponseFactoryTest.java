package com.cpmss.platform.common;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Verifies that {@link ErrorResponseFactory} produces correct
 * {@link ErrorResponse} envelopes for each error scenario.
 *
 * <p>Covers construction from {@link ApiException}, from raw
 * {@link com.cpmss.platform.exception.ErrorCode}, from validation
 * field maps, and from unexpected errors. Also verifies that the
 * request ID is read from MDC.
 */
@DisplayName("ErrorResponseFactory")
class ErrorResponseFactoryTest {

    @Test
    @DisplayName("fromApiException uses ErrorCode status and code")
    void fromApiExceptionBasic() {
        ApiException ex = new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        ErrorResponse response = ErrorResponseFactory.fromApiException(ex);

        assertEquals(404, response.status());
        assertEquals("RESOURCE_NOT_FOUND", response.code());
        assertEquals("Not Found", response.error());
        assertEquals("Requested resource not found", response.message());
        assertNull(response.fields());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("fromApiException with message override")
    void fromApiExceptionWithOverride() {
        ApiException ex = new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND,
                "Person with ID abc not found");
        ErrorResponse response = ErrorResponseFactory.fromApiException(ex);

        assertEquals(404, response.status());
        assertEquals("RESOURCE_NOT_FOUND", response.code());
        assertEquals("Person with ID abc not found", response.message());
    }

    @Test
    @DisplayName("fromApiException includes field errors when present")
    void fromApiExceptionWithFields() {
        Map<String, List<ApiException.FieldError>> fields = Map.of(
                "email", List.of(new ApiException.FieldError(
                        "PERSON_EMAIL_INVALID", "Email format is invalid"))
        );
        ApiException ex = new ApiException(CommonErrorCode.VALIDATION_FAILED, fields);
        ErrorResponse response = ErrorResponseFactory.fromApiException(ex);

        assertEquals(400, response.status());
        assertNotNull(response.fields());
        assertEquals(1, response.fields().size());
        assertEquals("PERSON_EMAIL_INVALID", response.fields().get("email").get(0).code());
    }

    @Test
    @DisplayName("fromErrorCode uses default message")
    void fromErrorCode() {
        ErrorResponse response = ErrorResponseFactory.fromErrorCode(
                CommonErrorCode.AUTHENTICATION_REQUIRED);

        assertEquals(401, response.status());
        assertEquals("AUTHENTICATION_REQUIRED", response.code());
        assertEquals("Unauthorized", response.error());
        assertEquals("Authentication is required", response.message());
        assertNull(response.fields());
    }

    @Test
    @DisplayName("fromValidationFields builds 400 response")
    void fromValidationFields() {
        Map<String, List<ApiException.FieldError>> fields = Map.of(
                "firstName", List.of(new ApiException.FieldError(
                        "VALIDATION_FIELD_INVALID", "must not be blank"))
        );
        ErrorResponse response = ErrorResponseFactory.fromValidationFields(fields);

        assertEquals(400, response.status());
        assertEquals("VALIDATION_FAILED", response.code());
        assertEquals("Request validation failed", response.message());
        assertNotNull(response.fields());
    }

    @Test
    @DisplayName("fromUnexpectedError hides internal details")
    void fromUnexpectedError() {
        ErrorResponse response = ErrorResponseFactory.fromUnexpectedError();

        assertEquals(500, response.status());
        assertEquals("UNEXPECTED_ERROR", response.code());
        assertEquals("An unexpected error occurred", response.message());
        assertNull(response.fields());
    }

    @Test
    @DisplayName("Request ID is read from MDC when present")
    void requestIdFromMdc() {
        try {
            MDC.put(ErrorResponseFactory.MDC_REQUEST_ID, "test-request-id");
            ErrorResponse response = ErrorResponseFactory.fromErrorCode(
                    CommonErrorCode.RESOURCE_NOT_FOUND);
            assertEquals("test-request-id", response.requestId());
        } finally {
            MDC.clear();
        }
    }

    @Test
    @DisplayName("Request ID is generated when MDC is empty")
    void requestIdGeneratedWhenMdcEmpty() {
        MDC.clear();
        ErrorResponse response = ErrorResponseFactory.fromErrorCode(
                CommonErrorCode.RESOURCE_NOT_FOUND);
        assertNotNull(response.requestId());
    }
}
