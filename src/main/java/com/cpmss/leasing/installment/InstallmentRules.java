package com.cpmss.leasing.installment;

import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;

/**
 * Business rules for {@link Installment} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see InstallmentService
 */
public class InstallmentRules {

    /**
     * Validates that the expected amount is positive.
     *
     * @param amountExpected the expected payment amount
     * @throws BusinessException if the amount is not positive
     */
    public void validateAmountPositive(BigDecimal amountExpected) {
        if (amountExpected == null || amountExpected.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Installment amount must be positive");
        }
    }

    /**
     * Validates installment status transitions.
     *
     * <p>Valid transitions:
     * <ul>
     *   <li>Pending → Partially Paid, Paid, Overdue, Cancelled</li>
     *   <li>Partially Paid → Paid, Overdue</li>
     *   <li>Overdue → Partially Paid, Paid, Cancelled</li>
     *   <li>Paid → (terminal)</li>
     *   <li>Cancelled → (terminal)</li>
     * </ul>
     *
     * @param currentStatus the current lifecycle status
     * @param newStatus     the requested new status
     * @throws BusinessException if the transition is invalid
     */
    public void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return;
        }
        boolean valid = switch (currentStatus) {
            case "Pending" -> "Partially Paid".equals(newStatus)
                    || "Paid".equals(newStatus)
                    || "Overdue".equals(newStatus)
                    || "Cancelled".equals(newStatus);
            case "Partially Paid" -> "Paid".equals(newStatus)
                    || "Overdue".equals(newStatus);
            case "Overdue" -> "Partially Paid".equals(newStatus)
                    || "Paid".equals(newStatus)
                    || "Cancelled".equals(newStatus);
            default -> false; // Paid and Cancelled are terminal
        };
        if (!valid) {
            throw new BusinessException(
                    "Invalid status transition: " + currentStatus + " → " + newStatus);
        }
    }
}
