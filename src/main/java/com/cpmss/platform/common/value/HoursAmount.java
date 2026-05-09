package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;

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
     * @throws ApiException if hours are missing, zero, or negative
     */
    public HoursAmount {
        if (hours == null) {
            throw new ApiException(CommonErrorCode.HOURS_AMOUNT_REQUIRED);
        }
        if (hours.signum() <= 0) {
            throw new ApiException(CommonErrorCode.HOURS_AMOUNT_INVALID);
        }
    }

    /**
     * Creates a positive hours amount.
     *
     * @param hours the raw hours amount
     * @return the validated hours amount
     * @throws ApiException if hours are missing, zero, or negative
     */
    public static HoursAmount positive(BigDecimal hours) {
        return new HoursAmount(hours);
    }
}
