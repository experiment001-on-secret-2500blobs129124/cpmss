package com.cpmss.workorder;

import com.cpmss.exception.BusinessException;

import java.math.BigDecimal;

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
     * @param costAmount the cost of the work (may be {@code null})
     * @throws BusinessException if the cost is not positive
     */
    public void validateCostPositive(BigDecimal costAmount) {
        if (costAmount != null && costAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Work order cost must be positive");
        }
    }

    /**
     * Validates work order status transitions.
     *
     * <p>Valid transitions:
     * <ul>
     *   <li>Open → In Progress, Cancelled</li>
     *   <li>In Progress → Completed, Cancelled</li>
     *   <li>Completed → (terminal)</li>
     *   <li>Cancelled → (terminal)</li>
     * </ul>
     *
     * @param currentStatus the current job status
     * @param newStatus     the requested new status
     * @throws BusinessException if the transition is invalid
     */
    public void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return;
        }
        boolean valid = switch (currentStatus) {
            case "Open" -> "In Progress".equals(newStatus)
                    || "Cancelled".equals(newStatus);
            case "In Progress" -> "Completed".equals(newStatus)
                    || "Cancelled".equals(newStatus);
            default -> false; // Completed and Cancelled are terminal
        };
        if (!valid) {
            throw new BusinessException(
                    "Invalid status transition: " + currentStatus + " → " + newStatus);
        }
    }
}
