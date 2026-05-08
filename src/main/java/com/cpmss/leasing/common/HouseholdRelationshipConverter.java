package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link HouseholdRelationship} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class HouseholdRelationshipConverter implements AttributeConverter<HouseholdRelationship, String> {

    /**
     * Converts the household relationship to its database label.
     *
     * @param attribute the relationship value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(HouseholdRelationship attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a household relationship.
     *
     * @param dbData the database label
     * @return the matching relationship, or {@code null} when absent
     */
    @Override
    public HouseholdRelationship convertToEntityAttribute(String dbData) {
        return dbData != null ? HouseholdRelationship.fromLabel(dbData) : null;
    }
}
