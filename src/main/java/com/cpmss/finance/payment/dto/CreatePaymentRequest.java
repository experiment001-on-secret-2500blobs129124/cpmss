package com.cpmss.finance.payment.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.finance.payment.PaymentNumber;
import com.cpmss.finance.payment.PaymentReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Base request payload for creating a payment.
 *
 * @param paymentNo            validated system-unique payment number
 * @param money                payment money with explicit amount and currency
 * @param paymentType          discriminator: Installment, WorkOrder, Payroll
 * @param method               payment method (Cash, Bank Transfer, Cheque, Card, Other)
 * @param direction            direction: Inbound or Outbound
 * @param referenceNo          optional validated external reference number
 * @param bankAccountId        the bank account UUID used
 * @param processedById        the staff member who processed this payment
 */
public record CreatePaymentRequest(
        @NotNull @Valid PaymentNumber paymentNo,
        @NotNull @Valid Money money,
        @NotBlank String paymentType,
        String method,
        @NotBlank String direction,
        @Valid PaymentReference referenceNo,
        @NotNull UUID bankAccountId,
        UUID processedById
) {}
