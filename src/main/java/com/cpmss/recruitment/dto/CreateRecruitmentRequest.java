package com.cpmss.recruitment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for scheduling an interview for an application.
 *
 * <p>Includes the application composite key (applicantId, positionId,
 * applicationDate) since Applications uses a composite PK rather
 * than a single UUID.
 *
 * @param applicantId     the applicant's person UUID (part of application PK)
 * @param positionId      the position UUID (part of application PK)
 * @param applicationDate the application date (part of application PK)
 * @param interviewerId   the person conducting the interview
 * @param interviewDate   the date of the interview
 */
public record CreateRecruitmentRequest(
        @NotNull UUID applicantId,
        @NotNull UUID positionId,
        @NotNull LocalDate applicationDate,
        @NotNull UUID interviewerId,
        @NotNull LocalDate interviewDate
) {}
