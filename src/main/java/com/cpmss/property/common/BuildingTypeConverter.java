package com.cpmss.property.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link BuildingType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class BuildingTypeConverter implements AttributeConverter<BuildingType, String> {

    /**
     * Converts the building type to its database label.
     *
     * @param attribute the building type value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(BuildingType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a building type.
     *
     * @param dbData the database label
     * @return the building type, or {@code null} when absent
     */
    @Override
    public BuildingType convertToEntityAttribute(String dbData) {
        return BuildingType.fromNullableLabel(dbData);
    }
}
