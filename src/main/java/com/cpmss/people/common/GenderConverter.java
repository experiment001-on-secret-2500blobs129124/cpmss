package com.cpmss.people.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link Gender} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class GenderConverter implements AttributeConverter<Gender, String> {

    /**
     * Converts the gender to its database label.
     *
     * @param attribute the gender value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a gender value.
     *
     * @param dbData the database label
     * @return the gender value, or {@code null} when absent
     */
    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return Gender.fromNullableLabel(dbData);
    }
}
