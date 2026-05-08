package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;

/**
 * Positive number of hours used by attendance and shift calculations.
 *
 * @param hours the positive hour quantity
 */
public record HoursAmount(BigDecimal hours) {

    /**
     * Creates a positive hours amount.
     *
     * @throws BusinessException if hours are missing, zero, or negative
     */
    public HoursAmount {
        if (hours == null) {
            throw new BusinessException("Hours amount is required");
        }
        if (hours.signum() <= 0) {
            throw new BusinessException("Hours amount must be positive");
        }
    }

    /**
     * Creates a positive hours amount.
     *
     * @param hours the raw hours amount
     * @return the validated hours amount
     * @throws BusinessException if hours are missing, zero, or negative
     */
    public static HoursAmount positive(BigDecimal hours) {
        return new HoursAmount(hours);
    }
}
