package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * System-unique payment number stored in {@code Payment.payment_no}.
 *
 * <p>The current schema stores the value in a 20-character column. This value
 * keeps the identifier non-blank, normalized, and limited to URL-safe
 * reference characters.
 *
 * @param value normalized payment number
 */
public record PaymentNumber(String value) {

    private static final int MAX_LENGTH = 20;

    /**
     * Creates a payment number.
     *
     * @throws BusinessException if the value is missing, too long, or contains
     *                           unsupported characters
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public PaymentNumber {
        value = normalize(value);
        if (value.length() > MAX_LENGTH) {
            throw new BusinessException("Payment number must be at most 20 characters");
        }
        if (!value.matches("[A-Z0-9][A-Z0-9._/-]*")) {
            throw new BusinessException("Payment number format is invalid");
        }
    }

    /**
     * Serializes this value as normalized payment number text.
     *
     * @return normalized payment number text
     */
    @JsonValue
    public String toJson() {
        return value;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Payment number is required");
        }
        return value.strip().toUpperCase(Locale.ROOT);
    }
}
