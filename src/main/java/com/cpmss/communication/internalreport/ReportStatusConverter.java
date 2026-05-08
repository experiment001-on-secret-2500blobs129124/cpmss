package com.cpmss.communication.internalreport;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ReportStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ReportStatusConverter implements AttributeConverter<ReportStatus, String> {

    /**
     * Converts the status to its database label.
     *
     * @param attribute the status value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ReportStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a status.
     *
     * @param dbData the database label
     * @return the matching status, or {@code null} when absent
     */
    @Override
    public ReportStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ReportStatus.fromLabel(dbData) : null;
    }
}
