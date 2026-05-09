package com.cpmss.platform.common.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link HoursAmount} to the decimal value stored by the schema.
 */
@Converter(autoApply = false)
public class HoursAmountConverter implements AttributeConverter<HoursAmount, BigDecimal> {

    /**
     * Converts the value object to its database decimal.
     *
     * @param attribute the hours amount value
     * @return the decimal hours amount, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(HoursAmount attribute) {
        return attribute != null ? attribute.hours() : null;
    }

    /**
     * Converts the database decimal to a value object.
     *
     * @param dbData the database hours amount
     * @return the hours amount, or {@code null} when absent
     */
    @Override
    public HoursAmount convertToEntityAttribute(BigDecimal dbData) {
        return dbData != null ? HoursAmount.positive(dbData) : null;
    }
}
