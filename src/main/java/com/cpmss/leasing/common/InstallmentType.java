package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Installment category stored in {@code Installment.installment_type}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_installment_type}
 * constraint.
 */
public enum InstallmentType {
    /** Regular rent installment. */
    RENT("Rent"),
    /** Security deposit installment. */
    DEPOSIT("Deposit"),
    /** Penalty installment. */
    PENALTY("Penalty"),
    /** Other installment category. */
    OTHER("Other");

    private final String label;

    InstallmentType(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in {@code installment_type}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required installment type label.
     *
     * @param label the installment type label
     * @return the matching type
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static InstallmentType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(LeasingErrorCode.INSTALLMENT_TYPE_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(LeasingErrorCode.INSTALLMENT_TYPE_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated installment type labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(InstallmentType::label)
                .collect(Collectors.joining(", "));
    }

    /**
     * Serializes this type as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
