package com.cpmss.application.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a job application.
 *
 * @param applicantId     the applicant's person UUID
 * @param positionId      the position UUID
 * @param applicationDate the date of the application
 */
public record ApplicationResponse(
        UUID applicantId,
        UUID positionId,
        LocalDate applicationDate
) {}
