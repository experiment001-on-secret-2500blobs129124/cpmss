package com.cpmss.security.accesspermit;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link AccessLevel} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class AccessLevelConverter implements AttributeConverter<AccessLevel, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the optional access level enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(AccessLevel attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the nullable database label
     * @return the matching access level, or {@code null} when the label is null
     */
    @Override
    public AccessLevel convertToEntityAttribute(String dbData) {
        return AccessLevel.fromNullableLabel(dbData);
    }
}
