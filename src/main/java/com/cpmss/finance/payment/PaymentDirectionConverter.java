package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PaymentDirection} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PaymentDirectionConverter implements AttributeConverter<PaymentDirection, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the payment direction enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(PaymentDirection attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching payment direction, or {@code null} when the label is
     *         null
     */
    @Override
    public PaymentDirection convertToEntityAttribute(String dbData) {
        return dbData != null ? PaymentDirection.fromLabel(dbData) : null;
    }
}
