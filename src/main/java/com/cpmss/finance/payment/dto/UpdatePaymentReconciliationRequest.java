package com.cpmss.finance.payment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for updating a payment reconciliation status.
 *
 * @param reconciliationStatus target reconciliation status label
 */
public record UpdatePaymentReconciliationRequest(
        @NotBlank String reconciliationStatus
) {}
