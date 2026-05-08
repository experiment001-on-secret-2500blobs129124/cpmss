package com.cpmss.performance.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PerformanceRating} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PerformanceRatingConverter implements AttributeConverter<PerformanceRating, String> {

    /**
     * Converts the rating to its database label.
     *
     * @param attribute the performance rating
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(PerformanceRating attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a rating value.
     *
     * @param dbData the database label
     * @return the performance rating, or {@code null} when absent
     */
    @Override
    public PerformanceRating convertToEntityAttribute(String dbData) {
        return PerformanceRating.fromNullableLabel(dbData);
    }
}
