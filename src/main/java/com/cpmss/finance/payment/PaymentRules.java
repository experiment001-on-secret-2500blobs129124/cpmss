package com.cpmss.finance.payment;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.exception.BusinessException;

import java.util.Set;

/**
 * Stateless business rules for payments.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Payment type must be one of the valid types</li>
 *   <li>Direction must be Inbound or Outbound</li>
 * </ul>
 *
 * <p>Amount and currency invariants live in {@link Money}. This class keeps
 * workflow-level vocabulary checks that still depend on the current payment
 * table discriminator columns.
 *
 * @see Money
 */
public class PaymentRules {

    private static final Set<String> VALID_TYPES = Set.of("Installment", "WorkOrder", "Payroll");
    private static final Set<String> VALID_DIRECTIONS = Set.of("Inbound", "Outbound");

    /**
     * Validates that the payment type is valid.
     *
     * @param paymentType the payment discriminator supplied by the workflow
     * @throws BusinessException if the type is not one of the supported
     *                           payment child-table categories
     */
    public void validatePaymentType(String paymentType) {
        if (!VALID_TYPES.contains(paymentType)) {
            throw new BusinessException(
                    "Payment type must be one of: " + VALID_TYPES);
        }
    }

    /**
     * Validates that the direction is valid.
     *
     * @param direction the payment direction supplied by the workflow
     * @throws BusinessException if the direction is not inbound or outbound
     */
    public void validateDirection(String direction) {
        if (!VALID_DIRECTIONS.contains(direction)) {
            throw new BusinessException(
                    "Payment direction must be 'Inbound' or 'Outbound'");
        }
    }
}
