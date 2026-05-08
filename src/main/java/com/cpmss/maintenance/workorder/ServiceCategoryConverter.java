package com.cpmss.maintenance.workorder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ServiceCategory} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ServiceCategoryConverter implements AttributeConverter<ServiceCategory, String> {

    /**
     * Converts the service category to its database label.
     *
     * @param attribute the service category value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ServiceCategory attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a service category.
     *
     * @param dbData the database label
     * @return the matching category, or {@code null} when absent
     */
    @Override
    public ServiceCategory convertToEntityAttribute(String dbData) {
        return ServiceCategory.fromNullableLabel(dbData);
    }
}
