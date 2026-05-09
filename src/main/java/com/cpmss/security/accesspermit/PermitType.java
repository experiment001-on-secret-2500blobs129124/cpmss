package com.cpmss.security.accesspermit;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Physical access permit format stored in {@code Access_Permit.permit_type}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_permit_type} constraint.
 */
public enum PermitType {
    /** Staff badge for compound employees. */
    STAFF_BADGE("Staff Badge"),
    /** Resident card linked to an active contract. */
    RESIDENT_CARD("Resident Card"),
    /** Visitor pass linked to an inviting person. */
    VISITOR_PASS("Visitor Pass"),
    /** Contractor pass linked to a work order. */
    CONTRACTOR_PASS("Contractor Pass"),
    /** Vehicle sticker linked to vehicle access. */
    VEHICLE_STICKER("Vehicle Sticker");

    private final String label;

    PermitType(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact permit type label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a permit type label.
     *
     * @param label the permit type label
     * @return the matching permit type
     * @throws ApiException if the label is missing or unsupported
     */
    public static PermitType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(SecurityErrorCode.PERMIT_TYPE_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(SecurityErrorCode.PERMIT_TYPE_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated permit type labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(PermitType::label).collect(Collectors.joining(", "));
    }
}
