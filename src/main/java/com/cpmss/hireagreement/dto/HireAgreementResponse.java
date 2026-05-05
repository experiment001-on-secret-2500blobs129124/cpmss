package com.cpmss.hireagreement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a hire agreement record.
 *
 * @param applicantId          the applicant's person UUID
 * @param positionId           the position UUID
 * @param applicationDate      the original application date
 * @param employmentStartDate  agreed start date
 * @param offeredBaseDailyRate agreed base daily rate
 * @param offeredMaximumSalary agreed maximum monthly salary
 */
public record HireAgreementResponse(
        UUID applicantId,
        UUID positionId,
        LocalDate applicationDate,
        LocalDate employmentStartDate,
        BigDecimal offeredBaseDailyRate,
        BigDecimal offeredMaximumSalary
) {}
