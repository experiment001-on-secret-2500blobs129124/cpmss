package com.cpmss.finance.bankaccount;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * SWIFT/BIC code used by bank-account records.
 *
 * <p>Valid values are 8-character institution BICs or 11-character branch
 * BICs. The value is normalized to uppercase before persistence.
 *
 * @param value normalized SWIFT/BIC code
 */
public record SwiftCode(String value) {

    /**
     * Creates a validated SWIFT/BIC code.
     *
     * @throws BusinessException if the code is missing or malformed
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SwiftCode {
        value = normalize(value);
        if (!value.matches("[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?")) {
            throw new BusinessException("SWIFT/BIC code format is invalid");
        }
    }

    /**
     * Creates an optional SWIFT/BIC code.
     *
     * @param value optional raw SWIFT/BIC text
     * @return the SWIFT/BIC value, or {@code null} when absent
     */
    public static SwiftCode optional(String value) {
        return value == null || value.isBlank() ? null : new SwiftCode(value);
    }

    /**
     * Serializes this value as normalized SWIFT/BIC text.
     *
     * @return normalized SWIFT/BIC text
     */
    @JsonValue
    public String toJson() {
        return value;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("SWIFT/BIC code is required");
        }
        return value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }
}
