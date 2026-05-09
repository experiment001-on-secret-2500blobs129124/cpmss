package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PaymentNumber} to the normalized payment number text.
 */
@Converter(autoApply = false)
public class PaymentNumberConverter implements AttributeConverter<PaymentNumber, String> {

    /**
     * Converts the payment number to its database text.
     *
     * @param attribute the payment number
     * @return normalized payment number text, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(PaymentNumber attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts database text to a payment number.
     *
     * @param dbData database payment number text
     * @return the payment number, or {@code null} when absent
     */
    @Override
    public PaymentNumber convertToEntityAttribute(String dbData) {
        return dbData != null ? new PaymentNumber(dbData) : null;
    }
}
