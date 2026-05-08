package com.cpmss.people.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request payload for updating a person.
 */
public record UpdatePersonRequest(
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
        List<CreatePersonRequest.PhoneEntry> phones,
        List<String> emails,
        List<UUID> roleIds
) {}
