package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PaymentType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PaymentTypeConverter implements AttributeConverter<PaymentType, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the payment type enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(PaymentType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching payment type, or {@code null} when the label is null
     */
    @Override
    public PaymentType convertToEntityAttribute(String dbData) {
        return dbData != null ? PaymentType.fromLabel(dbData) : null;
    }
}
