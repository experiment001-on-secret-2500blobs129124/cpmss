package com.cpmss.hr.staffposition.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for recording a position salary band.
 *
 * @param salaryEffectiveDate the date the salary band starts
 * @param maximumSalary       the maximum monthly salary for the position
 * @param baseDailyRate       the base daily rate used by payroll calculations
 */
public record CreatePositionSalaryHistoryRequest(
        @NotNull LocalDate salaryEffectiveDate,
        @NotNull BigDecimal maximumSalary,
        @NotNull BigDecimal baseDailyRate
) {}
