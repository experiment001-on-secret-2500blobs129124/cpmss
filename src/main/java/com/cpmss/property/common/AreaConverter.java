package com.cpmss.property.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link Area} to the decimal value stored by property tables.
 */
@Converter(autoApply = false)
public class AreaConverter implements AttributeConverter<Area, BigDecimal> {

    /**
     * Converts the area to its database value.
     *
     * @param attribute the area value
     * @return the decimal area, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(Area attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts a database decimal to an area value.
     *
     * @param dbData the database area value
     * @return the area value, or {@code null} when absent
     */
    @Override
    public Area convertToEntityAttribute(BigDecimal dbData) {
        return Area.optional(dbData);
    }
}
