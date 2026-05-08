package com.cpmss.people.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request payload for creating a person.
 *
 * @param passportNo         passport number
 * @param egyptianNationalId optional 14-digit Egyptian national ID
 * @param firstName          first name
 * @param middleName         optional middle name
 * @param lastName           last name
 * @param nationality        optional nationality
 * @param city               optional city
 * @param street             optional street
 * @param dateOfBirth        optional date of birth
 * @param gender             optional gender (Male/Female)
 * @param phones             list of phone entries
 * @param emails             list of email addresses
 * @param roleIds            role UUIDs to assign (≥1 required)
 */
public record CreatePersonRequest(
        @NotBlank @Size(max = 20) String passportNo,
        @Size(max = 14) String egyptianNationalId,
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String middleName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 50) String nationality,
        @Size(max = 50) String city,
        @Size(max = 150) String street,
        LocalDate dateOfBirth,
        @Size(max = 6) String gender,
        List<PhoneEntry> phones,
        List<String> emails,
        @NotEmpty List<UUID> roleIds
) {
    /**
     * A phone number entry with country code.
     *
     * @param countryCode the country dialing code
     * @param phone       the phone number
     */
    public record PhoneEntry(
            @NotBlank @Size(max = 5) String countryCode,
            @NotBlank @Size(max = 20) String phone
    ) {}
}
