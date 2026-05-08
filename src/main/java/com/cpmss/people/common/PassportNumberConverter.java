package com.cpmss.people.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PassportNumber} to the varchar value stored by Person.
 */
@Converter(autoApply = false)
public class PassportNumberConverter implements AttributeConverter<PassportNumber, String> {

    /**
     * Converts the passport number to its database value.
     *
     * @param attribute the passport number value
     * @return the passport string, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(PassportNumber attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts the database passport number to a value object.
     *
     * @param dbData the database passport value
     * @return the passport number, or {@code null} when absent
     */
    @Override
    public PassportNumber convertToEntityAttribute(String dbData) {
        return dbData != null ? PassportNumber.of(dbData) : null;
    }
}
