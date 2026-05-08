package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Optional external bank, cheque, card, or processor reference for a payment.
 *
 * <p>This value does not impose provider-specific formats; it only prevents
 * blank references and keeps the value within the database column length.
 *
 * @param value trimmed external reference text
 */
public record PaymentReference(String value) {

    private static final int MAX_LENGTH = 100;

    /**
     * Creates a payment reference.
     *
     * @throws BusinessException if the reference is blank or too long
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public PaymentReference {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Payment reference is required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new BusinessException("Payment reference must be at most 100 characters");
        }
    }

    /**
     * Creates an optional payment reference.
     *
     * @param value optional raw reference text
     * @return the payment reference, or {@code null} when absent
     */
    public static PaymentReference optional(String value) {
        return value == null || value.isBlank() ? null : new PaymentReference(value);
    }

    /**
     * Serializes this value as trimmed reference text.
     *
     * @return trimmed reference text
     */
    @JsonValue
    public String toJson() {
        return value;
    }
}
