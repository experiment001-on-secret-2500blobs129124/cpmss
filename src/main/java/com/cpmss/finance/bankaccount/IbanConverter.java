package com.cpmss.finance.bankaccount;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link Iban} to the normalized string stored by bank accounts.
 */
@Converter(autoApply = false)
public class IbanConverter implements AttributeConverter<Iban, String> {

    /**
     * Converts the IBAN value to its database text.
     *
     * @param attribute the IBAN value
     * @return normalized IBAN text, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(Iban attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts database text to an IBAN value.
     *
     * @param dbData database IBAN text
     * @return the IBAN value, or {@code null} when absent
     */
    @Override
    public Iban convertToEntityAttribute(String dbData) {
        return Iban.optional(dbData);
    }
}
