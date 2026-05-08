package com.cpmss.hr.staffsalaryhistory;

import com.cpmss.platform.exception.BusinessException;

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
     * @throws BusinessException if rate is not positive
     */
    public void validateBaseDailyRatePositive(BigDecimal baseDailyRate) {
        if (baseDailyRate == null || baseDailyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Base daily rate must be positive");
        }
    }

    /**
     * Validates that the maximum salary is positive.
     *
     * @param maximumSalary the proposed maximum
     * @throws BusinessException if maximum is not positive
     */
    public void validateMaximumSalaryPositive(BigDecimal maximumSalary) {
        if (maximumSalary == null || maximumSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Maximum salary must be positive");
        }
    }
}
