package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ContractType} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ContractTypeConverter implements AttributeConverter<ContractType, String> {

    /**
     * Converts the contract type to its database label.
     *
     * @param attribute the contract type value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ContractType attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a contract type.
     *
     * @param dbData the database label
     * @return the matching type, or {@code null} when absent
     */
    @Override
    public ContractType convertToEntityAttribute(String dbData) {
        return dbData != null ? ContractType.fromLabel(dbData) : null;
    }
}
