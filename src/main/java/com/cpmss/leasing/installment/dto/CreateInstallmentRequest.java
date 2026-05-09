package com.cpmss.leasing.installment.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.InstallmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating an installment.
 *
 * @param installmentType   the category (Rent, Deposit, Penalty, Other)
 * @param dueDate           when this installment is due
 * @param installmentStatus lifecycle status (Pending, Partially Paid, Paid, Overdue, Cancelled)
 * @param amountExpected    expected payment money (must be positive)
 * @param contractId        the contract this installment belongs to
 */
public record CreateInstallmentRequest(
        @NotNull InstallmentType installmentType,
        @NotNull LocalDate dueDate,
        @NotNull InstallmentStatus installmentStatus,
        @NotNull @Valid Money amountExpected,
        @NotNull UUID contractId
) {}
