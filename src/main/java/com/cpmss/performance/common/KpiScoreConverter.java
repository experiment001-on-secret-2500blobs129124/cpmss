package com.cpmss.performance.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Converts {@link KpiScore} to the decimal value stored by the schema.
 */
@Converter(autoApply = false)
public class KpiScoreConverter implements AttributeConverter<KpiScore, BigDecimal> {

    /**
     * Converts the score to its database decimal.
     *
     * @param attribute the KPI score value
     * @return the decimal score, or {@code null} when absent
     */
    @Override
    public BigDecimal convertToDatabaseColumn(KpiScore attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts the database decimal to a score value.
     *
     * @param dbData the database score
     * @return the KPI score, or {@code null} when absent
     */
    @Override
    public KpiScore convertToEntityAttribute(BigDecimal dbData) {
        return KpiScore.nullable(dbData);
    }
}
