package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PaymentReference} to external reference text.
 */
@Converter(autoApply = false)
public class PaymentReferenceConverter implements AttributeConverter<PaymentReference, String> {

    /**
     * Converts the payment reference to database text.
     *
     * @param attribute the payment reference
     * @return reference text, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(PaymentReference attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts database text to a payment reference.
     *
     * @param dbData database reference text
     * @return the payment reference, or {@code null} when absent
     */
    @Override
    public PaymentReference convertToEntityAttribute(String dbData) {
        return PaymentReference.optional(dbData);
    }
}
