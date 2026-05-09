package com.cpmss.hr.staffsalaryhistory;

import com.cpmss.hr.compensation.SalaryAmount;

import java.math.BigDecimal;

/**
 * Stateless business rules for staff salary history changes.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Base daily rate must be positive</li>
 *   <li>Maximum salary must be positive</li>
 *   <li>Maximum salary must not be less than the position's maximum salary</li>
 * </ul>
 *
 * @see StaffSalaryHistory
 */
public class StaffSalaryRules {

    /**
     * Validates that the base daily rate is positive.
     *
     * @param baseDailyRate the proposed rate
     * @throws com.cpmss.platform.exception.ApiException if rate is not
     *                                      positive
     */
    public void validateBaseDailyRatePositive(BigDecimal baseDailyRate) {
        SalaryAmount.positive(baseDailyRate);
    }

    /**
     * Validates that the maximum salary is positive.
     *
     * @param maximumSalary the proposed maximum
     * @throws com.cpmss.platform.exception.ApiException if maximum is not
     *                                      positive
     */
    public void validateMaximumSalaryPositive(BigDecimal maximumSalary) {
        SalaryAmount.positive(maximumSalary);
    }
}
