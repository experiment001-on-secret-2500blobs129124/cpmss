package com.cpmss.communication.internalreport;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ReportPriority} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ReportPriorityConverter implements AttributeConverter<ReportPriority, String> {

    /**
     * Converts the priority to its database label.
     *
     * @param attribute the priority value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ReportPriority attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a priority.
     *
     * @param dbData the database label
     * @return the matching priority, or {@code null} when absent
     */
    @Override
    public ReportPriority convertToEntityAttribute(String dbData) {
        return ReportPriority.fromNullableLabel(dbData);
    }
}
