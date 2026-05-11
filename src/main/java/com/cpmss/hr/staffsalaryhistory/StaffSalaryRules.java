package com.cpmss.hr.staffsalaryhistory;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.compensation.SalaryAmount;
import com.cpmss.platform.exception.ApiException;

import java.math.BigDecimal;

/**
 * Stateless business rules for staff salary history changes.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Base daily rate must be positive</li>
 *   <li>Maximum salary must be positive</li>
 *   <li>Staff maximum salary must not exceed the position salary band</li>
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

    /**
     * Validates that a staff salary is within the active position salary band.
     *
     * @param staffMaximumSalary the staff member's proposed maximum salary
     * @param positionMaximumSalary the active position maximum salary
     * @throws ApiException if the staff maximum exceeds the position band
     */
    public void validateWithinPositionMaximum(BigDecimal staffMaximumSalary,
                                              BigDecimal positionMaximumSalary) {
        SalaryAmount staffMaximum = SalaryAmount.positive(staffMaximumSalary);
        SalaryAmount positionMaximum = SalaryAmount.positive(positionMaximumSalary);
        if (staffMaximum.amount().compareTo(positionMaximum.amount()) > 0) {
            throw new ApiException(HrErrorCode.STAFF_SALARY_EXCEEDS_POSITION_MAX);
        }
    }
}
