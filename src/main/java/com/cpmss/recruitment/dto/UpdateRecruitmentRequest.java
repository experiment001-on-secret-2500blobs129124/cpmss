package com.cpmss.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for recording an interview result.
 *
 * <p>Includes the full 5-part composite key to identify the
 * specific interview record, since Recruitment has no single UUID PK.
 *
 * @param interviewerId   the interviewer's person UUID
 * @param applicantId     the applicant's person UUID
 * @param positionId      the position UUID
 * @param applicationDate the original application date
 * @param interviewDate   the interview date
 * @param interviewResult the outcome: Pass, Fail, or Pending
 */
public record UpdateRecruitmentRequest(
        @NotNull UUID interviewerId,
        @NotNull UUID applicantId,
        @NotNull UUID positionId,
        @NotNull LocalDate applicationDate,
        @NotNull LocalDate interviewDate,
        @NotBlank String interviewResult
) {}
