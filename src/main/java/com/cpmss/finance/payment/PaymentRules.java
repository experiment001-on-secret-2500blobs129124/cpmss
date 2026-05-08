package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Stateless business rules for payments.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Amount must be positive</li>
 *   <li>Payment type must be one of the valid types</li>
 *   <li>Direction must be Inbound or Outbound</li>
 * </ul>
 */
public class PaymentRules {

    private static final Set<String> VALID_TYPES = Set.of("Installment", "WorkOrder", "Payroll");
    private static final Set<String> VALID_DIRECTIONS = Set.of("Inbound", "Outbound");

    /**
     * Validates that the payment amount is positive.
     */
    public void validateAmountPositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Payment amount must be positive");
        }
    }

    /**
     * Validates that the payment type is valid.
     */
    public void validatePaymentType(String paymentType) {
        if (!VALID_TYPES.contains(paymentType)) {
            throw new BusinessException(
                    "Payment type must be one of: " + VALID_TYPES);
        }
    }

    /**
     * Validates that the direction is valid.
     */
    public void validateDirection(String direction) {
        if (!VALID_DIRECTIONS.contains(direction)) {
            throw new BusinessException(
                    "Payment direction must be 'Inbound' or 'Outbound'");
        }
    }
}
