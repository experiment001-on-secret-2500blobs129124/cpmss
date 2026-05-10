package com.cpmss.hr.staffposition.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for position salary band history.
 *
 * @param positionId          the position
 * @param salaryEffectiveDate the date this salary band starts
 * @param maximumSalary       the maximum monthly salary
 * @param baseDailyRate       the base daily payroll rate
 */
public record PositionSalaryHistoryResponse(
        UUID positionId,
        LocalDate salaryEffectiveDate,
        BigDecimal maximumSalary,
        BigDecimal baseDailyRate
) {}
