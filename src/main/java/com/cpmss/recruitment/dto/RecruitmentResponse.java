package com.cpmss.recruitment.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a recruitment (interview) record.
 *
 * @param interviewerId   the interviewer's person UUID
 * @param applicantId     the applicant's person UUID
 * @param positionId      the position UUID
 * @param applicationDate the original application date
 * @param interviewDate   the interview date
 * @param interviewResult the outcome (Pass, Fail, Pending, or null)
 */
public record RecruitmentResponse(
        UUID interviewerId,
        UUID applicantId,
        UUID positionId,
        LocalDate applicationDate,
        LocalDate interviewDate,
        String interviewResult
) {}
