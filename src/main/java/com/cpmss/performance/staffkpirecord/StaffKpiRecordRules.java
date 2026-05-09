package com.cpmss.performance.staffkpirecord;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.performance.kpipolicy.KpiPolicy;

import java.time.LocalDate;

/**
 * Stateless business rules for daily KPI recording.
 *
 * <p>Enforces:
 * <ul>
 *   <li>KPI policy must be active for the recording date</li>
 *   <li>KPI score must be non-negative (handled by @PositiveOrZero on DTO)</li>
 * </ul>
 */
public class StaffKpiRecordRules {

    /**
     * Validates that the KPI policy is active for the given date.
     *
     * @param policy     the KPI policy
     * @param recordDate the date of the KPI assessment
     * @throws ApiException if the policy is not active for that date
     */
    public void validatePolicyActiveForDate(KpiPolicy policy, LocalDate recordDate) {
        if (policy.getEffectiveDate().isAfter(recordDate)) {
            throw new ApiException(PerformanceErrorCode.KPI_POLICY_NOT_ACTIVE);
        }
    }
}
