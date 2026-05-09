package com.cpmss.performance.kpipolicy;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.performance.common.KpiScoreRange;

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
     * Validates that min score is strictly less than max score.
     *
     * @param minKpiScore the minimum KPI score
     * @param maxKpiScore the maximum KPI score
     * @return the validated score range
     * @throws ApiException if either score is missing, negative, or if
     *                           max is not greater than min
     */
    public KpiScoreRange validateScoreRange(BigDecimal minKpiScore, BigDecimal maxKpiScore) {
        return KpiScoreRange.of(minKpiScore, maxKpiScore);
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
     * @throws ApiException if tiers overlap
     */
    public void validateNoTierOverlap(BigDecimal newMin, BigDecimal newMax,
                                       boolean overlapExists) {
        if (overlapExists) {
            throw new ApiException(PerformanceErrorCode.KPI_TIER_OVERLAP);
        }
    }
}
