package com.cpmss.hr.compensation;

import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.math.BigDecimal;

/**
 * Positive compensation amount stored in salary-related decimal columns.
 *
 * <p>The schema currently stores salary amounts without currency columns, so
 * this value object validates the amount only. Currency-aware payroll money
 * requires an explicit schema decision before it can replace these fields.
 *
 * @param amount the positive salary amount
 */
public record SalaryAmount(BigDecimal amount) {

    /**
     * Creates a positive salary amount.
     *
     * @throws ApiException if the amount is missing, zero, or negative
     */
    public SalaryAmount {
        if (amount == null) {
            throw new ApiException(HrErrorCode.SALARY_REQUIRED);
        }
        if (amount.signum() <= 0) {
            throw new ApiException(HrErrorCode.SALARY_NOT_POSITIVE);
        }
    }

    /**
     * Creates a positive salary amount.
     *
     * @param amount the raw salary amount
     * @return the validated salary amount
     * @throws ApiException if the amount is missing, zero, or negative
     */
    public static SalaryAmount positive(BigDecimal amount) {
        return new SalaryAmount(amount);
    }

    /**
     * Creates a nullable positive salary amount.
     *
     * @param amount the optional raw salary amount
     * @return the validated salary amount, or {@code null} when absent
     * @throws ApiException if the amount is present but not positive
     */
    public static SalaryAmount nullablePositive(BigDecimal amount) {
        return amount != null ? positive(amount) : null;
    }
}
