package com.cpmss.payrollpayment.dto;

import com.cpmss.payment.dto.CreatePaymentRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * Request payload for creating a payroll payment.
 *
 * @param payment      the base payment details
 * @param staffId      the staff member receiving the payroll
 * @param departmentId the department UUID
 * @param year         the payroll year
 * @param month        the payroll month
 */
public record CreatePayrollPaymentRequest(
        @NotNull @Valid CreatePaymentRequest payment,
        @NotNull UUID staffId,
        @NotNull UUID departmentId,
        @NotNull Integer year,
        @NotNull Integer month
) {}
