package com.cpmss.security.accesspermit;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link PermitType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class PermitTypeConverter implements AttributeConverter<PermitType, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the permit type enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(PermitType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching permit type, or {@code null} when the label is null
     */
    @Override
    public PermitType convertToEntityAttribute(String dbData) {
        return dbData != null ? PermitType.fromLabel(dbData) : null;
    }
}
