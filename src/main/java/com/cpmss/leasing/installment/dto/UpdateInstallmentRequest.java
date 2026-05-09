package com.cpmss.leasing.installment.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.InstallmentStatus;
import com.cpmss.leasing.common.InstallmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request payload for updating an existing installment.
 *
 * <p>Contract association is immutable — cannot be changed after creation.
 *
 * @param installmentType   the category
 * @param dueDate           when this installment is due
 * @param installmentStatus lifecycle status
 * @param amountExpected    expected payment money (must be positive)
 */
public record UpdateInstallmentRequest(
        @NotNull InstallmentType installmentType,
        @NotNull LocalDate dueDate,
        @NotNull InstallmentStatus installmentStatus,
        @NotNull @Valid Money amountExpected
) {}
