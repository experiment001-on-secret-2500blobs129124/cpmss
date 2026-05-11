package com.cpmss.performance.kpipolicy;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreRange;
import com.cpmss.platform.exception.ApiException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
     * Validates that a new tier does not overlap with existing tiers in the
     * same department and policy effective-date version.
     *
     * <p>Two inclusive tiers overlap if their ranges share at least one score.
     * The current policy row is ignored during updates.
     *
     * @param proposedRange the proposed tier range
     * @param existingTiers tiers that share the department and effective date
     * @param currentPolicyId the policy row being updated, or {@code null} on create
     * @throws ApiException if tiers overlap
     */
    public void validateNoTierOverlap(KpiScoreRange proposedRange,
                                      List<KpiPolicy> existingTiers,
                                      UUID currentPolicyId) {
        boolean overlapExists = existingTiers.stream()
                .filter(tier -> currentPolicyId == null || !currentPolicyId.equals(tier.getId()))
                .map(KpiPolicy::getScoreRange)
                .anyMatch(proposedRange::overlaps);
        if (overlapExists) {
            throw new ApiException(PerformanceErrorCode.KPI_TIER_OVERLAP);
        }
    }

    /**
     * Validates that a KPI policy belongs to the requested department.
     *
     * @param policy the selected policy tier
     * @param departmentId the requested department UUID
     * @throws ApiException if the policy belongs to another department
     */
    public void validatePolicyDepartment(KpiPolicy policy, UUID departmentId) {
        if (!policy.getDepartment().getId().equals(departmentId)) {
            throw new ApiException(PerformanceErrorCode.KPI_POLICY_DEPARTMENT_MISMATCH);
        }
    }

    /**
     * Validates a selected KPI policy tier against the target workflow scope.
     *
     * @param policy the selected policy tier
     * @param departmentId the department where the score is recorded
     * @param activeEffectiveDate the active policy version date for the score date
     * @param scoreRange the selected policy's score range
     * @param score the score being recorded
     * @throws ApiException if the policy does not belong to the department,
     *                      is not from the active policy version, or does not
     *                      contain the score
     */
    public void validatePolicyMatchesRecord(KpiPolicy policy,
                                            UUID departmentId,
                                            LocalDate activeEffectiveDate,
                                            KpiScoreRange scoreRange,
                                            KpiScore score) {
        validatePolicyDepartment(policy, departmentId);
        if (!policy.getEffectiveDate().equals(activeEffectiveDate)) {
            throw new ApiException(PerformanceErrorCode.KPI_POLICY_NOT_ACTIVE);
        }
        if (!scoreRange.contains(score)) {
            throw new ApiException(PerformanceErrorCode.KPI_POLICY_SCORE_OUT_OF_RANGE);
        }
    }
}
