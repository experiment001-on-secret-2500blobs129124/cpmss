package com.cpmss.security.vehicle;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link LicensePlate} to its normalized database string.
 */
@Converter(autoApply = false)
public class LicensePlateConverter implements AttributeConverter<LicensePlate, String> {

    /**
     * Converts the value object to its database string.
     *
     * @param attribute the license plate value
     * @return the normalized plate string, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(LicensePlate attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts the database string to a license plate value.
     *
     * @param dbData the database plate string
     * @return the normalized plate value, or {@code null} when absent
     */
    @Override
    public LicensePlate convertToEntityAttribute(String dbData) {
        return LicensePlate.ofNullable(dbData);
    }
}
