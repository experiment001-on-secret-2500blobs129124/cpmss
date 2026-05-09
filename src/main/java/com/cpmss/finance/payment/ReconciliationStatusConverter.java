package com.cpmss.finance.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ReconciliationStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ReconciliationStatusConverter implements AttributeConverter<ReconciliationStatus, String> {

    /**
     * Converts the enum value to its database label.
     *
     * @param attribute the reconciliation status enum value
     * @return the database label, or {@code null} when the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(ReconciliationStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to the enum value.
     *
     * @param dbData the database label
     * @return the matching reconciliation status, or {@code null} when the
     *         label is null
     */
    @Override
    public ReconciliationStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ReconciliationStatus.fromLabel(dbData) : null;
    }
}
