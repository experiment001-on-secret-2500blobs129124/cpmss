package com.cpmss.identity.auth.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for applicant self-registration.
 *
 * @param user            created applicant account
 * @param personId        created person UUID
 * @param positionId      applied position UUID
 * @param applicationDate application date
 */
public record ApplicantRegistrationResponse(
        AppUserResponse user,
        UUID personId,
        UUID positionId,
        LocalDate applicationDate
) {}
