package com.cpmss.finance.installmentpayment.dto;

import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request payload for creating an installment payment.
 *
 * @param payment       the base payment details
 * @param installmentId the installment UUID being paid
 * @param lateFeeAmount optional late fee amount
 */
public record CreateInstallmentPaymentRequest(
        @NotNull @Valid CreatePaymentRequest payment,
        @NotNull UUID installmentId,
        BigDecimal lateFeeAmount
) {}
