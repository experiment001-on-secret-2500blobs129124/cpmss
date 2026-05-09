package com.cpmss.finance.installmentpayment.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for creating an installment payment.
 *
 * @param payment       the base payment details
 * @param installmentId the installment UUID being paid
 * @param lateFee       optional late fee money
 */
public record CreateInstallmentPaymentRequest(
        @NotNull @Valid CreatePaymentRequest payment,
        @NotNull UUID installmentId,
        @Valid Money lateFee
) {}
