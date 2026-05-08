package com.cpmss.workforce.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link HourDelta} to the decimal value stored by attendance rows.
 */
@Converter(autoApply = false)
public class HourDeltaConverter implements AttributeConverter<HourDelta, BigDecimal> {

    /**
     * Converts the hour delta to its database decimal.
     *
     * @param attribute the hour delta value
     * @return the decimal hour delta, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(HourDelta attribute) {
        return attribute != null ? attribute.hours() : null;
    }

    /**
     * Converts a database decimal to an hour delta.
     *
     * @param dbData the database hour delta
     * @return the hour delta, or {@code null} when absent
     */
    @Override
    public HourDelta convertToEntityAttribute(BigDecimal dbData) {
        return HourDelta.optional(dbData);
    }
}
