package com.cpmss.communication.internalreport;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ReportCategory} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ReportCategoryConverter implements AttributeConverter<ReportCategory, String> {

    /**
     * Converts the category to its database label.
     *
     * @param attribute the category value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ReportCategory attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a category.
     *
     * @param dbData the database label
     * @return the matching category, or {@code null} when absent
     */
    @Override
    public ReportCategory convertToEntityAttribute(String dbData) {
        return dbData != null ? ReportCategory.fromLabel(dbData) : null;
    }
}
