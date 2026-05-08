package com.cpmss.leasing.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link ContractPartyRole} to the exact database label used by Flyway.
 */
@Converter(autoApply = false)
public class ContractPartyRoleConverter implements AttributeConverter<ContractPartyRole, String> {

    /**
     * Converts the party role to its database label.
     *
     * @param attribute the party role value
     * @return the database label, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(ContractPartyRole attribute) {
        return attribute != null ? attribute.label() : null;
    }

    /**
     * Converts the database label to a party role.
     *
     * @param dbData the database label
     * @return the matching role, or {@code null} when absent
     */
    @Override
    public ContractPartyRole convertToEntityAttribute(String dbData) {
        return dbData != null ? ContractPartyRole.fromLabel(dbData) : null;
    }
}
