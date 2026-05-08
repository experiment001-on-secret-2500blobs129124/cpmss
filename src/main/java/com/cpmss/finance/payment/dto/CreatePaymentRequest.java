package com.cpmss.finance.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Base request payload for creating a payment.
 *
 * @param paymentNo            system-unique payment number
 * @param amount               payment amount (must be positive)
 * @param currency             currency code (default USD)
 * @param paymentType          discriminator: Installment, WorkOrder, Payroll
 * @param method               payment method (Cash, Bank Transfer, Cheque, Card, Other)
 * @param direction            direction: Inbound or Outbound
 * @param referenceNo          external reference number
 * @param bankAccountId        the bank account UUID used
 * @param processedById        the staff member who processed this payment
 */
public record CreatePaymentRequest(
        @NotBlank String paymentNo,
        @NotNull @Positive BigDecimal amount,
        String currency,
        @NotBlank String paymentType,
        String method,
        @NotBlank String direction,
        String referenceNo,
        @NotNull UUID bankAccountId,
        UUID processedById
) {}
