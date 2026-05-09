package com.cpmss.platform.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the {@link ErrorCode} interface contract and the
 * {@link CommonErrorCode} enum's structural invariants.
 *
 * <p>Ensures every error code carries a valid HTTP status, a non-empty
 * default message, and a code name matching its enum constant.
 */
@DisplayName("ErrorCode contract and CommonErrorCode")
class ErrorCodeTest {

    @Test
    @DisplayName("Every CommonErrorCode has a valid HTTP status")
    void everyCodeHasValidHttpStatus() {
        Set<Integer> validStatuses = Set.of(400, 401, 403, 404, 409, 422, 500);
        for (CommonErrorCode code : CommonErrorCode.values()) {
            assertTrue(validStatuses.contains(code.status()),
                    code.name() + " has unexpected status " + code.status());
        }
    }

    @Test
    @DisplayName("Every CommonErrorCode has a non-empty default message")
    void everyCodeHasDefaultMessage() {
        for (CommonErrorCode code : CommonErrorCode.values()) {
            assertNotNull(code.defaultMessage(),
                    code.name() + " has null default message");
            assertFalse(code.defaultMessage().isBlank(),
                    code.name() + " has blank default message");
        }
    }

    @Test
    @DisplayName("code() returns the enum constant name")
    void codeReturnsEnumName() {
        for (CommonErrorCode code : CommonErrorCode.values()) {
            assertEquals(code.name(), code.code(),
                    "code() must match name() for " + code.name());
        }
    }

    @Test
    @DisplayName("No duplicate code strings across CommonErrorCode")
    void noDuplicateCodes() {
        Set<String> codes = Arrays.stream(CommonErrorCode.values())
                .map(CommonErrorCode::code)
                .collect(Collectors.toSet());
        assertEquals(CommonErrorCode.values().length, codes.size(),
                "Duplicate code strings found in CommonErrorCode");
    }

    @Test
    @DisplayName("ApiException with ErrorCode uses default message")
    void apiExceptionUsesDefaultMessage() {
        ApiException ex = new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        assertEquals("Requested resource not found", ex.getMessage());
        assertEquals(CommonErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals(404, ex.getErrorCode().status());
    }

    @Test
    @DisplayName("ApiException with override message preserves code but uses custom message")
    void apiExceptionOverrideMessage() {
        ApiException ex = new ApiException(CommonErrorCode.RESOURCE_NOT_FOUND,
                "Person with ID abc not found");
        assertEquals("Person with ID abc not found", ex.getMessage());
        assertEquals(CommonErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode().code());
    }
}
