package com.cpmss.performance.common;

import com.cpmss.platform.exception.ApiException;

import java.math.BigDecimal;

/**
 * Non-negative KPI score stored in KPI score decimal columns.
 *
 * @param value the non-negative score value
 */
public record KpiScore(BigDecimal value) {

    /**
     * Creates a non-negative KPI score.
     *
     * @param value the raw score value
     * @throws ApiException if the score is missing or negative
     */
    public KpiScore {
        if (value == null) {
            throw new ApiException(PerformanceErrorCode.KPI_SCORE_REQUIRED);
        }
        if (value.signum() < 0) {
            throw new ApiException(PerformanceErrorCode.KPI_SCORE_NEGATIVE);
        }
    }

    /**
     * Creates a non-negative KPI score.
     *
     * @param value the raw score value
     * @return the validated KPI score
     * @throws ApiException if the score is missing or negative
     */
    public static KpiScore of(BigDecimal value) {
        return new KpiScore(value);
    }

    /**
     * Creates a nullable non-negative KPI score.
     *
     * @param value the optional raw score value
     * @return the validated KPI score, or {@code null} when absent
     * @throws ApiException if the score is present but negative
     */
    public static KpiScore nullable(BigDecimal value) {
        return value != null ? of(value) : null;
    }
}
