package com.cpmss.security.gate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link GateStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class GateStatusConverter implements AttributeConverter<GateStatus, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the optional gate status enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(GateStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the nullable database label
     * @return the matching gate status, or {@code null} when the label is null
     */
    @Override
    public GateStatus convertToEntityAttribute(String dbData) {
        return GateStatus.fromNullableLabel(dbData);
    }
}
