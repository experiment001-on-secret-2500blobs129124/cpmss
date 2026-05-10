package com.cpmss.identity.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Public request payload for applicant self-registration and first application.
 *
 * @param email           applicant login and contact email
 * @param password        applicant initial password
 * @param passportNo      required passport number for Person identity
 * @param firstName       applicant first name
 * @param lastName        applicant last name
 * @param countryCode     applicant phone country code
 * @param phone           applicant phone number
 * @param positionId      position being applied for
 * @param applicationDate application date, defaults to today when omitted
 */
public record RegisterApplicantRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 20) String passportNo,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 5) String countryCode,
        @NotBlank @Size(max = 20) String phone,
        @NotNull UUID positionId,
        LocalDate applicationDate
) {}
