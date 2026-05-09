package com.cpmss.leasing.installment;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.LeasingErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @param amountExpected the expected payment money
     * @throws ApiException if the amount is not positive
     */
    public void validateAmountPositive(Money amountExpected) {
        if (amountExpected == null || amountExpected.getAmount().signum() <= 0) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_AMOUNT_NOT_POSITIVE);
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
     * @param newStatus the requested new status
     * @throws ApiException if the transition is invalid
     */
    public void validateStatusTransition(InstallmentStatus currentStatus, InstallmentStatus newStatus) {
        if (currentStatus == null || newStatus == null || !currentStatus.canTransitionTo(newStatus)) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_STATUS_TRANSITION_INVALID);
        }
    }

    private String label(InstallmentStatus status) {
        return status != null ? status.label() : "null";
    }
}
