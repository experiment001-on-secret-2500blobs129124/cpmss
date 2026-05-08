package com.cpmss.performance.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link PercentageRate} to the decimal value stored by the schema.
 */
@Converter(autoApply = false)
public class PercentageRateConverter implements AttributeConverter<PercentageRate, BigDecimal> {

    /**
     * Converts the rate to its database decimal.
     *
     * @param attribute the percentage rate value
     * @return the decimal rate, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(PercentageRate attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts the database decimal to a rate value.
     *
     * @param dbData the database rate
     * @return the percentage rate, or {@code null} when absent
     */
    @Override
    public PercentageRate convertToEntityAttribute(BigDecimal dbData) {
        return PercentageRate.nullable(dbData);
    }
}
