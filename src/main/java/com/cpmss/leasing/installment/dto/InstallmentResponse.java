package com.cpmss.leasing.installment.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.InstallmentType;

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
 * @param amountExpected    expected payment money
 * @param contractId        the owning contract UUID
 * @param createdAt         when the installment was created
 * @param updatedAt         when the installment was last modified
 */
public record InstallmentResponse(
        UUID id,
        InstallmentType installmentType,
        LocalDate dueDate,
        InstallmentStatus installmentStatus,
        Money amountExpected,
        UUID contractId,
        Instant createdAt,
        Instant updatedAt
) {}
