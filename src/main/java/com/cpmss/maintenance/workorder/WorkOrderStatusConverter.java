package com.cpmss.maintenance.workorder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link WorkOrderStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class WorkOrderStatusConverter implements AttributeConverter<WorkOrderStatus, String> {

    /**
     * Converts the status to its database label.
     *
     * @param attribute the status value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(WorkOrderStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a status.
     *
     * @param dbData the database label
     * @return the matching status, or {@code null} when absent
     */
    @Override
    public WorkOrderStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? WorkOrderStatus.fromLabel(dbData) : null;
    }
}
