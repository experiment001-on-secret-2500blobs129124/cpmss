package com.cpmss.finance.payment;

import com.cpmss.finance.common.FinanceErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Payment method stored in {@code Payment.method}.
 *
 * <p>The column is nullable, but any non-null value must match the Flyway V2
 * {@code chk_payment_method} constraint exactly.
 */
public enum PaymentMethod {
    /** Physical cash payment. */
    CASH("Cash"),
    /** Bank transfer payment. */
    BANK_TRANSFER("Bank Transfer"),
    /** Cheque payment. */
    CHEQUE("Cheque"),
    /** Card payment. */
    CARD("Card"),
    /** Other recognized payment method. */
    OTHER("Other");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    /**
     * Returns the database label for this payment method.
     *
     * @return the exact label stored in {@code method}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a nullable database/API label into a payment method.
     *
     * @param label the optional payment method label
     * @return the matching payment method, or {@code null} when the label is
     *         {@code null}
     * @throws ApiException if the label is blank or unsupported
     */
    public static PaymentMethod fromNullableLabel(String label) {
        if (label == null) {
            return null;
        }
        if (label.isBlank()) {
            throw new ApiException(FinanceErrorCode.PAYMENT_METHOD_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(FinanceErrorCode.PAYMENT_METHOD_INVALID));
    }

    /**
     * Lists the labels accepted by the payment method database constraint.
     *
     * @return a comma-separated list of accepted labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(PaymentMethod::label)
                .collect(Collectors.joining(", "));
    }
}
