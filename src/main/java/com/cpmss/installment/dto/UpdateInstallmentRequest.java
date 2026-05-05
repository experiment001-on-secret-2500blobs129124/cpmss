package com.cpmss.installment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for updating an existing installment.
 *
 * <p>Contract association is immutable — cannot be changed after creation.
 *
 * @param installmentType   the category
 * @param dueDate           when this installment is due
 * @param installmentStatus lifecycle status
 * @param amountExpected    expected payment amount (must be positive)
 */
public record UpdateInstallmentRequest(
        @NotBlank @Size(max = 50) String installmentType,
        @NotNull LocalDate dueDate,
        @NotBlank @Size(max = 50) String installmentStatus,
        @NotNull @Positive BigDecimal amountExpected
) {}
