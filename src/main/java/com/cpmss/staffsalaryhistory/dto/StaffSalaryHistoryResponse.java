package com.cpmss.staffsalaryhistory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a salary history record.
 *
 * @param staffId       the staff member's person UUID
 * @param effectiveDate the date this rate became effective
 * @param endDate       the date this rate was superseded (null if still active)
 * @param baseDailyRate the base daily rate
 * @param maximumSalary the maximum monthly salary cap
 * @param approvedById  the manager who authorized this change
 * @param reviewId      the performance review that triggered this change
 */
public record StaffSalaryHistoryResponse(
        UUID staffId,
        LocalDate effectiveDate,
        LocalDate endDate,
        BigDecimal baseDailyRate,
        BigDecimal maximumSalary,
        UUID approvedById,
        UUID reviewId
) {}
