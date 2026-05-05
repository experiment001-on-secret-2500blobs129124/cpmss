package com.cpmss.workorderpayment.dto;

import com.cpmss.payment.dto.CreatePaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for creating a work order payment.
 *
 * @param payment     the base payment details
 * @param workOrderId the work order UUID being paid
 * @param invoiceNo   vendor invoice number
 */
public record CreateWorkOrderPaymentRequest(
        @NotNull @Valid CreatePaymentRequest payment,
        @NotNull UUID workOrderId,
        String invoiceNo
) {}
