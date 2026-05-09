package com.cpmss.performance.common;

import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;

/**
 * Inclusive KPI score range for a policy tier.
 *
 * @param min the inclusive lower score
 * @param max the inclusive upper score
 */
public record KpiScoreRange(KpiScore min, KpiScore max) {

    /**
     * Creates a KPI score range.
     *
     * @param min the inclusive lower score
     * @param max the inclusive upper score
     * @throws BusinessException if either bound is missing or max is not
     *                           greater than min
     */
    public KpiScoreRange {
        if (min == null || max == null) {
            throw new BusinessException("KPI score range bounds are required");
        }
        if (max.value().compareTo(min.value()) <= 0) {
            throw new BusinessException("KPI score range max must be greater than min");
        }
    }

    /**
     * Creates a score range from raw decimal values.
     *
     * @param min the inclusive lower score
     * @param max the inclusive upper score
     * @return the validated KPI score range
     * @throws BusinessException if either bound is missing or invalid, or if
     *                           max is not greater than min
     */
    public static KpiScoreRange of(BigDecimal min, BigDecimal max) {
        return new KpiScoreRange(KpiScore.of(min), KpiScore.of(max));
    }

    /**
     * Checks whether the score falls inside the inclusive range.
     *
     * @param score the score to check
     * @return true when the score is within the range
     * @throws BusinessException if the score is missing
     */
    public boolean contains(KpiScore score) {
        if (score == null) {
            throw new BusinessException("KPI score is required");
        }
        return score.value().compareTo(min.value()) >= 0
                && score.value().compareTo(max.value()) <= 0;
    }
}
