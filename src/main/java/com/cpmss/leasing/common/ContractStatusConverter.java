package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ContractStatus} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ContractStatusConverter implements AttributeConverter<ContractStatus, String> {

    /**
     * Converts the contract status to its database label.
     *
     * @param attribute the contract status value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ContractStatus attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a contract status.
     *
     * @param dbData the database label
     * @return the matching status, or {@code null} when absent
     */
    @Override
    public ContractStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ContractStatus.fromLabel(dbData) : null;
    }
}
