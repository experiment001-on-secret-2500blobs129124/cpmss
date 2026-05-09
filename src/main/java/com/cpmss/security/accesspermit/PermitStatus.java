package com.cpmss.security.accesspermit;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Access permit lifecycle state stored in {@code Access_Permit.permit_status}.
 *
 * <p>Permits are revoked by moving to {@link #REVOKED}, not by deleting the
 * row. Labels must match the Flyway V2 {@code chk_permit_status} constraint.
 */
public enum PermitStatus {
    /** Permit is usable when date and ownership checks also pass. */
    ACTIVE("Active"),
    /** Permit has passed its expiry date. */
    EXPIRED("Expired"),
    /** Permit is temporarily suspended. */
    SUSPENDED("Suspended"),
    /** Permit has been permanently revoked. */
    REVOKED("Revoked");

    private final String label;

    PermitStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact permit status label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a permit status label.
     *
     * @param label the permit status label
     * @return the matching permit status
     * @throws ApiException if the label is missing or unsupported
     */
    public static PermitStatus fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(SecurityErrorCode.PERMIT_STATUS_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(SecurityErrorCode.PERMIT_STATUS_REQUIRED));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated permit status labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(PermitStatus::label).collect(Collectors.joining(", "));
    }
}
