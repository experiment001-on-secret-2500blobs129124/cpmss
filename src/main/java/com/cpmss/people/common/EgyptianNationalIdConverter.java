package com.cpmss.people.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link EgyptianNationalId} to the varchar value stored by Person.
 */
@Converter(autoApply = false)
public class EgyptianNationalIdConverter implements AttributeConverter<EgyptianNationalId, String> {

    /**
     * Converts the national ID to its database value.
     *
     * @param attribute the national ID value
     * @return the 14-digit ID, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(EgyptianNationalId attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts the database national ID to a value object.
     *
     * @param dbData the database national ID
     * @return the national ID value, or {@code null} when absent
     */
    @Override
    public EgyptianNationalId convertToEntityAttribute(String dbData) {
        return EgyptianNationalId.nullable(dbData);
    }
}
