package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link InstallmentStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class InstallmentStatusConverter implements AttributeConverter<InstallmentStatus, String> {

    /**
     * Converts the installment status to its database label.
     *
     * @param attribute the installment status value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(InstallmentStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to an installment status.
     *
     * @param dbData the database label
     * @return the matching status, or {@code null} when absent
     */
    @Override
    public InstallmentStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? InstallmentStatus.fromLabel(dbData) : null;
    }
}
