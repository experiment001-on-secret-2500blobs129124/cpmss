package com.cpmss.performance.staffkpimonthlysummary;

import com.cpmss.platform.exception.BusinessException;

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
     * @throws BusinessException if no closer specified
     */
    public void validateCloserProvided(boolean closedById) {
        if (!closedById) {
            throw new BusinessException(
                    "Monthly KPI close requires a manager or HR officer (closedById)");
        }
    }
}
