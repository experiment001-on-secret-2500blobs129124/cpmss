package com.cpmss.maintenance.workorder;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.exception.BusinessException;

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
     * @throws BusinessException if the cost is not positive
     */
    public void validateCostPositive(Money cost) {
        if (cost != null && cost.getAmount().signum() <= 0) {
            throw new BusinessException("Work order cost must be positive");
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
     * @throws BusinessException if the transition is invalid
     */
    public void validateStatusTransition(WorkOrderStatus currentStatus, WorkOrderStatus newStatus) {
        if (currentStatus == null || newStatus == null || !currentStatus.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    "Invalid status transition: " + label(currentStatus) + " → " + label(newStatus));
        }
    }

    private String label(WorkOrderStatus status) {
        return status != null ? status.label() : "null";
    }
}
