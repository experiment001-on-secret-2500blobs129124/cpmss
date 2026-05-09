package com.cpmss.property.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link UnitStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class UnitStatusConverter implements AttributeConverter<UnitStatus, String> {

    /**
     * Converts the unit status to its database label.
     *
     * @param attribute the unit status value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(UnitStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a unit status.
     *
     * @param dbData the database label
     * @return the matching status, or {@code null} when absent
     */
    @Override
    public UnitStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? UnitStatus.fromLabel(dbData) : null;
    }
}
