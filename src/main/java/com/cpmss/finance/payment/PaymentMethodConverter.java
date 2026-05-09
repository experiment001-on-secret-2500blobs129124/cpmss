package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PaymentMethod} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the optional payment method enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the nullable database label
     * @return the matching payment method, or {@code null} when the label is
     *         null
     */
    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        return PaymentMethod.fromNullableLabel(dbData);
    }
}
