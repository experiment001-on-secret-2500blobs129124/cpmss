package com.cpmss.hireagreement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a hire agreement for a successful applicant.
 *
 * <p>The system will also create StaffProfile, StaffPositionHistory,
 * and StaffSalaryHistory in the same transaction (US-1 step 7).
 *
 * @param applicantId          the applicant's person UUID
 * @param positionId           the position UUID
 * @param applicationDate      the original application date
 * @param employmentStartDate  agreed start date
 * @param offeredBaseDailyRate agreed base daily rate
 * @param offeredMaximumSalary agreed maximum monthly salary (nullable)
 * @param qualificationId      qualification for the staff profile
 */
public record CreateHireAgreementRequest(
        @NotNull UUID applicantId,
        @NotNull UUID positionId,
        @NotNull LocalDate applicationDate,
        @NotNull LocalDate employmentStartDate,
        @NotNull @Positive BigDecimal offeredBaseDailyRate,
        BigDecimal offeredMaximumSalary,
        @NotNull UUID qualificationId
) {}
