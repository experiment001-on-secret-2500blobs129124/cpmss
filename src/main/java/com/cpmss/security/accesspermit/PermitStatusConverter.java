package com.cpmss.security.accesspermit;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PermitStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PermitStatusConverter implements AttributeConverter<PermitStatus, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the permit status enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(PermitStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching permit status, or {@code null} when the label is null
     */
    @Override
    public PermitStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? PermitStatus.fromLabel(dbData) : null;
    }
}
