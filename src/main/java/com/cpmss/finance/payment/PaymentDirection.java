package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Direction of a ledger movement stored in {@code Payment.direction}.
 *
 * <p>Inbound means money received by the compound. Outbound means money paid
 * out by the compound. Labels must match the Flyway V2 payment direction
 * check constraint.
 */
public enum PaymentDirection {
    /** Money received by the compound. */
    INBOUND("Inbound"),
    /** Money paid out by the compound. */
    OUTBOUND("Outbound");

    private final String label;

    PaymentDirection(String label) {
        this.label = label;
    }

    /**
     * Returns the database label for this payment direction.
     *
     * @return the exact label stored in {@code direction}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a database/API label into a payment direction.
     *
     * @param label the payment direction label
     * @return the matching payment direction
     * @throws BusinessException if the label is missing or unsupported
     */
    public static PaymentDirection fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Payment direction is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Payment direction must be one of: " + allowedLabels()));
    }

    /**
     * Lists the labels accepted by the payment direction database constraint.
     *
     * @return a comma-separated list of accepted labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(PaymentDirection::label)
                .collect(Collectors.joining(", "));
    }
}
