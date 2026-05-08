package com.cpmss.finance.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a payment record.
 *
 * @param paymentId            the payment UUID
 * @param paymentNo            system-unique payment number
 * @param paidAt               timestamp of the payment
 * @param amount               payment amount
 * @param currency             currency code
 * @param paymentType          discriminator (Installment, WorkOrder, Payroll)
 * @param method               payment method (Cash, Bank Transfer, Cheque, Card, Other)
 * @param direction            direction (Inbound, Outbound)
 * @param referenceNo          external reference number
 * @param reconciliationStatus reconciliation status (Pending, Reconciled, Disputed)
 * @param bankAccountId        the bank account UUID
 * @param processedById        the staff member who processed this payment
 */
public record PaymentResponse(
        UUID paymentId,
        String paymentNo,
        Instant paidAt,
        BigDecimal amount,
        String currency,
        String paymentType,
        String method,
        String direction,
        String referenceNo,
        String reconciliationStatus,
        UUID bankAccountId,
        UUID processedById
) {}
