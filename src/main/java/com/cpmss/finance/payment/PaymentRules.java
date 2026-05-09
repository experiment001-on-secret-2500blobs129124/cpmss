package com.cpmss.finance.payment;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.exception.BusinessException;

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

    /**
     * Validates that the payment type is valid.
     *
     * @param paymentType the payment discriminator supplied by the workflow
     * @return the typed payment discriminator
     * @throws BusinessException if the type is not a supported child-table
     *                           category
     */
    public PaymentType validatePaymentType(String paymentType) {
        return PaymentType.fromLabel(paymentType);
    }

    /**
     * Validates that the optional payment method is valid when supplied.
     *
     * @param method the optional payment method label supplied by the workflow
     * @return the typed payment method, or {@code null} when none was supplied
     * @throws BusinessException if the method label is blank or unsupported
     */
    public PaymentMethod validateMethod(String method) {
        return PaymentMethod.fromNullableLabel(method);
    }

    /**
     * Validates that the direction is valid.
     *
     * @param direction the payment direction supplied by the workflow
     * @return the typed payment direction
     * @throws BusinessException if the direction is not inbound or outbound
     */
    public PaymentDirection validateDirection(String direction) {
        return PaymentDirection.fromLabel(direction);
    }
}
