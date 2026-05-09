package com.cpmss.performance.common;

import com.cpmss.platform.exception.ApiException;

import java.math.BigDecimal;

/**
 * Non-negative percentage or multiplier rate used by KPI payroll policies.
 *
 * @param value the non-negative rate value
 */
public record PercentageRate(BigDecimal value) {

    /** Zero rate used by policy defaults and missing optional rate inputs. */
    public static final PercentageRate ZERO = new PercentageRate(BigDecimal.ZERO);

    /**
     * Creates a non-negative rate.
     *
     * @param value the raw rate value
     * @throws ApiException if the rate is missing or negative
     */
    public PercentageRate {
        if (value == null) {
            throw new ApiException(PerformanceErrorCode.PERCENTAGE_RATE_REQUIRED);
        }
        if (value.signum() < 0) {
            throw new ApiException(PerformanceErrorCode.PERCENTAGE_RATE_NEGATIVE);
        }
    }

    /**
     * Creates a non-negative rate.
     *
     * @param value the raw rate value
     * @return the validated rate
     * @throws ApiException if the rate is missing or negative
     */
    public static PercentageRate of(BigDecimal value) {
        return new PercentageRate(value);
    }

    /**
     * Creates a nullable non-negative rate.
     *
     * @param value the optional raw rate value
     * @return the validated rate, or {@code null} when absent
     * @throws ApiException if the rate is present but negative
     */
    public static PercentageRate nullable(BigDecimal value) {
        return value != null ? of(value) : null;
    }

    /**
     * Creates a non-negative rate, defaulting missing values to zero.
     *
     * @param value the optional raw rate value
     * @return the validated rate, or {@link #ZERO} when absent
     * @throws ApiException if the rate is present but negative
     */
    public static PercentageRate orZero(BigDecimal value) {
        return value != null ? of(value) : ZERO;
    }
}
