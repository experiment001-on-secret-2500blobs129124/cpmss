package com.cpmss.installment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating an installment.
 *
 * @param installmentType   the category (Rent, Deposit, Penalty, Other)
 * @param dueDate           when this installment is due
 * @param installmentStatus lifecycle status (Pending, Partially Paid, Paid, Overdue, Cancelled)
 * @param amountExpected    expected payment amount (must be positive)
 * @param contractId        the contract this installment belongs to
 */
public record CreateInstallmentRequest(
        @NotBlank @Size(max = 50) String installmentType,
        @NotNull LocalDate dueDate,
        @NotBlank @Size(max = 50) String installmentStatus,
        @NotNull @Positive BigDecimal amountExpected,
        @NotNull UUID contractId
) {}
