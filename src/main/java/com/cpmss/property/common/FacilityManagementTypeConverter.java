package com.cpmss.property.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link FacilityManagementType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class FacilityManagementTypeConverter implements AttributeConverter<FacilityManagementType, String> {

    /**
     * Converts the management type to its database label.
     *
     * @param attribute the management type value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(FacilityManagementType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a management type.
     *
     * @param dbData the database label
     * @return the matching management type, or {@code null} when absent
     */
    @Override
    public FacilityManagementType convertToEntityAttribute(String dbData) {
        return dbData != null ? FacilityManagementType.fromLabel(dbData) : null;
    }
}
