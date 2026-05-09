package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link InstallmentType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class InstallmentTypeConverter implements AttributeConverter<InstallmentType, String> {

    /**
     * Converts the installment type to its database label.
     *
     * @param attribute the installment type value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(InstallmentType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to an installment type.
     *
     * @param dbData the database label
     * @return the matching type, or {@code null} when absent
     */
    @Override
    public InstallmentType convertToEntityAttribute(String dbData) {
        return dbData != null ? InstallmentType.fromLabel(dbData) : null;
    }
}
