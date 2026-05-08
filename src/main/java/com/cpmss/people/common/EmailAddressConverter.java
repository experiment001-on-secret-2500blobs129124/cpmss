package com.cpmss.people.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts {@link EmailAddress} to the varchar value stored by the schema.
 */
@Converter(autoApply = false)
public class EmailAddressConverter implements AttributeConverter<EmailAddress, String> {

    /**
     * Converts the email address to its database value.
     *
     * @param attribute the email address value
     * @return the normalized email string, or {@code null} when absent
     */
    @Override
    public String convertToDatabaseColumn(EmailAddress attribute) {
        return attribute != null ? attribute.value() : null;
    }

    /**
     * Converts a database email string to a value object.
     *
     * @param dbData the database email string
     * @return the email address value, or {@code null} when absent
     */
    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData != null ? EmailAddress.of(dbData) : null;
    }
}
