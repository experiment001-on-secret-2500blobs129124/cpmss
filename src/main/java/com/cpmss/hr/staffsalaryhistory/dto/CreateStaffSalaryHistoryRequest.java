package com.cpmss.hr.staffsalaryhistory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a new salary history record (raise/change).
 *
 * @param staffId       the staff member's person UUID
 * @param effectiveDate the date the new rate takes effect
 * @param baseDailyRate the new base daily rate
 * @param maximumSalary the new maximum monthly salary cap
 * @param approvedById  the manager who authorized this change (null for initial hire)
 * @param reviewId      the performance review that triggered this change (nullable)
 */
public record CreateStaffSalaryHistoryRequest(
        @NotNull UUID staffId,
        @NotNull LocalDate effectiveDate,
        @NotNull @Positive BigDecimal baseDailyRate,
        @NotNull @Positive BigDecimal maximumSalary,
        UUID approvedById,
        UUID reviewId
) {}
