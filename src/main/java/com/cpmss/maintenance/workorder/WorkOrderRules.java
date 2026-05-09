package com.cpmss.maintenance.workorder;

import com.cpmss.finance.money.Money;
import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.platform.exception.ApiException;

/**
 * Business rules for {@link WorkOrder} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see WorkOrderService
 */
public class WorkOrderRules {

    /**
     * Validates that the cost amount is positive when provided.
     *
     * @param cost the cost of the work (may be {@code null})
     * @throws ApiException if the cost is not positive
     */
    public void validateCostPositive(Money cost) {
        if (cost != null && cost.getAmount().signum() <= 0) {
            throw new ApiException(MaintenanceErrorCode.WORK_ORDER_COST_NOT_POSITIVE);
        }
    }

    /**
     * Validates work order status transitions.
     *
     * <p>Valid transitions:
     * <ul>
     *   <li>Pending → Assigned, In Progress, Cancelled</li>
     *   <li>Assigned → In Progress, Cancelled</li>
     *   <li>In Progress → Completed, Cancelled</li>
     *   <li>Completed → Paid</li>
     *   <li>Paid/Cancelled → terminal</li>
     * </ul>
     *
     * @param currentStatus the current job status
     * @param newStatus     the requested new status
     * @throws ApiException if the transition is invalid
     */
    public void validateStatusTransition(WorkOrderStatus currentStatus, WorkOrderStatus newStatus) {
        if (currentStatus == null || newStatus == null || !currentStatus.canTransitionTo(newStatus)) {
            throw new ApiException(MaintenanceErrorCode.WORK_ORDER_STATUS_TRANSITION_INVALID);
        }
    }
}
