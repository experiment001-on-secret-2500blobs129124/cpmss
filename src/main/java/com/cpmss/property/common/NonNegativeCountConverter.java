package com.cpmss.property.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link NonNegativeCount} to the integer stored by property tables.
 */
@Converter(autoApply = false)
public class NonNegativeCountConverter implements AttributeConverter<NonNegativeCount, Integer> {

    /**
     * Converts the count to its database integer.
     *
     * @param attribute the count value
     * @return the integer count, or {@code null} when absent
     */
    @Override
    public Integer convertToDatabaseColumn(NonNegativeCount attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts a database integer to a count value.
     *
     * @param dbData the database count
     * @return the count value, or {@code null} when absent
     */
    @Override
    public NonNegativeCount convertToEntityAttribute(Integer dbData) {
        return NonNegativeCount.optional(dbData);
    }
}
