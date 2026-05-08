package com.cpmss.finance.payment;

import com.cpmss.platform.exception.BusinessException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Reconciliation lifecycle state stored in {@code Payment.reconciliation_status}.
 *
 * <p>Labels must stay aligned with the Flyway V2
 * {@code chk_reconciliation_status} constraint.
 */
public enum ReconciliationStatus {
    /** Payment is waiting for reconciliation. */
    PENDING("Pending"),
    /** Payment has been reconciled against bank or ledger evidence. */
    RECONCILED("Reconciled"),
    /** Payment reconciliation is disputed. */
    DISPUTED("Disputed");

    private final String label;

    ReconciliationStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the database label for this reconciliation status.
     *
     * @return the exact label stored in {@code reconciliation_status}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a database/API label into a reconciliation status.
     *
     * @param label the reconciliation status label
     * @return the matching reconciliation status
     * @throws BusinessException if the label is missing or unsupported
     */
    public static ReconciliationStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new BusinessException("Reconciliation status is required");
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Reconciliation status must be one of: " + allowedLabels()));
    }

    /**
     * Lists labels accepted by the reconciliation status database constraint.
     *
     * @return a comma-separated list of accepted labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(ReconciliationStatus::label)
                .collect(Collectors.joining(", "));
    }
}
