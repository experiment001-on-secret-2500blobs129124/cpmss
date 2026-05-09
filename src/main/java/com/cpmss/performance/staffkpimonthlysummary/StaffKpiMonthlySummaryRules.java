package com.cpmss.performance.staffkpimonthlysummary;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;

/**
 * Stateless business rules for monthly KPI summary close.
 *
 * <p>Enforces:
 * <ul>
 *   <li>closed_by must be provided (manager/HR who authorized the close)</li>
 * </ul>
 */
public class StaffKpiMonthlySummaryRules {

    /**
     * Validates that a closer (manager/HR) is specified.
     *
     * @param closedById whether the closer ID is present
     * @throws ApiException if no closer specified
     */
    public void validateCloserProvided(boolean closedById) {
        if (!closedById) {
            throw new ApiException(PerformanceErrorCode.KPI_CLOSER_REQUIRED);
        }
    }
}
