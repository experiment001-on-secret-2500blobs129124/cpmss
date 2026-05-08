package com.cpmss.finance.payrollpayment.dto;

import com.cpmss.finance.payment.dto.CreatePaymentRequest;
import com.cpmss.platform.common.value.YearMonthPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * Request payload for creating a payroll payment.
 *
 * @param payment      the base payment details
 * @param staffId      the staff member receiving the payroll
 * @param departmentId the department UUID
 * @param payrollPeriod the payroll period
 */
public record CreatePayrollPaymentRequest(
        @NotNull @Valid CreatePaymentRequest payment,
        @NotNull UUID staffId,
        @NotNull UUID departmentId,
        @NotNull @Valid YearMonthPeriod payrollPeriod
) {}
