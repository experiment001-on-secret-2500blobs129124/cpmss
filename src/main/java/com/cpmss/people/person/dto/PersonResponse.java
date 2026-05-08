package com.cpmss.people.person.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response payload for a person.
 */
public record PersonResponse(
        UUID id,
        String passportNo,
        String egyptianNationalId,
        String firstName,
        String middleName,
        String lastName,
        String nationality,
        String city,
        String street,
        LocalDate dateOfBirth,
        String gender,
        Boolean isBlacklisted,
        List<PhoneEntry> phones,
        List<String> emails,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
    public record PhoneEntry(String countryCode, String phone) {}
}
