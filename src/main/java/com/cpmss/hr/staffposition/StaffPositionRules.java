package com.cpmss.hr.staffposition;

import com.cpmss.hr.compensation.SalaryAmount;

import java.math.BigDecimal;

/**
 * Business rules for {@link StaffPosition} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see StaffPositionService
 */
public class StaffPositionRules {

    /**
     * Validates salary-band amounts for a position.
     *
     * @param maximumSalary the maximum monthly salary for the position
     * @param baseDailyRate the base daily rate for the position
     * @return validated salary amounts in maximum/base order
     */
    public PositionSalaryBand validatePositionSalaryBand(BigDecimal maximumSalary,
                                                         BigDecimal baseDailyRate) {
        return new PositionSalaryBand(
                SalaryAmount.positive(maximumSalary),
                SalaryAmount.positive(baseDailyRate));
    }

    /**
     * Validated position salary-band values.
     *
     * @param maximumSalary the positive maximum salary amount
     * @param baseDailyRate the positive base daily salary amount
     */
    public record PositionSalaryBand(SalaryAmount maximumSalary, SalaryAmount baseDailyRate) {
    }
}
