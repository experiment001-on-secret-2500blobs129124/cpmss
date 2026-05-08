package com.cpmss.performance.kpipolicy;

import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;

/**
 * Business rules for {@link KpiPolicy} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see KpiPolicyService
 */
public class KpiPolicyRules {

    /**
     * Validates that min score is less than or equal to max score.
     *
     * @param minKpiScore the minimum KPI score
     * @param maxKpiScore the maximum KPI score
     * @throws BusinessException if min exceeds max
     */
    public void validateScoreRange(BigDecimal minKpiScore, BigDecimal maxKpiScore) {
        if (minKpiScore.compareTo(maxKpiScore) > 0) {
            throw new BusinessException(
                    "Min KPI score (" + minKpiScore + ") must be ≤ max KPI score ("
                            + maxKpiScore + ")");
        }
    }

    /**
     * Validates that a new tier does not overlap with existing tiers
     * in the same department.
     *
     * <p>Two tiers overlap if their [min, max] ranges intersect.
     *
     * @param newMin  the proposed tier's min score
     * @param newMax  the proposed tier's max score
     * @param overlapExists whether an overlapping tier already exists
     * @throws BusinessException if tiers overlap
     */
    public void validateNoTierOverlap(BigDecimal newMin, BigDecimal newMax,
                                       boolean overlapExists) {
        if (overlapExists) {
            throw new BusinessException(
                    "KPI tier [" + newMin + ", " + newMax
                            + "] overlaps with an existing tier in this department");
        }
    }
}
