package com.cpmss.finance.payment;

import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Payment subtype discriminator stored in {@code Payment.payment_type}.
 *
 * <p>The labels must remain byte-for-byte compatible with the Flyway V2
 * {@code chk_payment_type} constraint because the database stores these
 * values as strings.
 */
public enum PaymentType {
    /** Resident installment payment subtype. */
    INSTALLMENT("Installment"),
    /** Vendor work-order payment subtype. */
    WORK_ORDER("WorkOrder"),
    /** Staff payroll payment subtype. */
    PAYROLL("Payroll");

    private final String label;

    PaymentType(String label) {
        this.label = label;
    }

    /**
     * Returns the database label for this payment type.
     *
     * @return the exact label stored in {@code payment_type}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a database/API label into a payment type.
     *
     * @param label the payment type label
     * @return the matching payment type
     * @throws ApiException if the label is missing or unsupported
     */
    public static PaymentType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(FinanceErrorCode.PAYMENT_TYPE_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(FinanceErrorCode.PAYMENT_TYPE_INVALID));
    }

    /**
     * Lists the labels accepted by the payment type database constraint.
     *
     * @return a comma-separated list of accepted labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(PaymentType::label)
                .collect(Collectors.joining(", "));
    }
}
