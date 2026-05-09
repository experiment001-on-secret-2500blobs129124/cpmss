package com.cpmss.hr.compensation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link SalaryAmount} to the decimal value stored by the schema.
 */
@Converter(autoApply = false)
public class SalaryAmountConverter implements AttributeConverter<SalaryAmount, BigDecimal> {

    /**
     * Converts the value object to its database decimal.
     *
     * @param attribute the salary amount value
     * @return the decimal amount, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(SalaryAmount attribute) {
        return attribute != null ? attribute.amount() : null;
    }

    /**
     * Converts the database decimal to a value object.
     *
     * @param dbData the database amount
     * @return the salary amount, or {@code null} when absent
     */
    @Override
    public SalaryAmount convertToEntityAttribute(BigDecimal dbData) {
        return SalaryAmount.nullablePositive(dbData);
    }
}
