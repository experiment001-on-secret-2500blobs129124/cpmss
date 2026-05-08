package com.cpmss.finance.bankaccount;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link SwiftCode} to the normalized string stored by bank accounts.
 */
@Converter(autoApply = false)
public class SwiftCodeConverter implements AttributeConverter<SwiftCode, String> {

    /**
     * Converts the SWIFT/BIC value to its database text.
     *
     * @param attribute the SWIFT/BIC value
     * @return normalized SWIFT/BIC text, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(SwiftCode attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts database text to a SWIFT/BIC value.
     *
     * @param dbData database SWIFT/BIC text
     * @return the SWIFT/BIC value, or {@code null} when absent
     */
    @Override
    public SwiftCode convertToEntityAttribute(String dbData) {
        return SwiftCode.optional(dbData);
    }
}
