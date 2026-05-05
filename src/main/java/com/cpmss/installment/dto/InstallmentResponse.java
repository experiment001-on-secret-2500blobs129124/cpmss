package com.cpmss.installment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for an installment.
 *
 * @param id                the installment's UUID primary key
 * @param installmentType   the category (Rent, Deposit, Penalty, Other)
 * @param dueDate           when this installment is due
 * @param installmentStatus lifecycle status
 * @param amountExpected    expected payment amount
 * @param contractId        the owning contract UUID
 * @param createdAt         when the installment was created
 * @param updatedAt         when the installment was last modified
 */
public record InstallmentResponse(
        UUID id,
        String installmentType,
        LocalDate dueDate,
        String installmentStatus,
        BigDecimal amountExpected,
        UUID contractId,
        Instant createdAt,
        Instant updatedAt
) {}
