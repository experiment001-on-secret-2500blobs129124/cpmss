package com.cpmss.security.entersat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link GateDirection} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class GateDirectionConverter implements AttributeConverter<GateDirection, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the gate direction enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(GateDirection attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching gate direction, or {@code null} when the label is null
     */
    @Override
    public GateDirection convertToEntityAttribute(String dbData) {
        return dbData != null ? GateDirection.fromLabel(dbData) : null;
    }
}
