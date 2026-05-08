package com.cpmss.maintenance.workorder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link WorkOrderPriority} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class WorkOrderPriorityConverter implements AttributeConverter<WorkOrderPriority, String> {

    /**
     * Converts the priority to its database label.
     *
     * @param attribute the priority value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(WorkOrderPriority attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a priority.
     *
     * @param dbData the database label
     * @return the matching priority, or {@code null} when absent
     */
    @Override
    public WorkOrderPriority convertToEntityAttribute(String dbData) {
        return WorkOrderPriority.fromNullableLabel(dbData);
    }
}
