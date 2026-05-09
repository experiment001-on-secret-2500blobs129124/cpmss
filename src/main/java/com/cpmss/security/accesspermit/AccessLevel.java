package com.cpmss.security.accesspermit;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Access tier stored in {@code Access_Permit.access_level}.
 *
 * <p>The column is nullable, but any non-null value must match the Flyway V2
 * {@code chk_access_level} constraint exactly.
 */
public enum AccessLevel {
    /** Full access to permitted compound areas. */
    FULL_ACCESS("Full Access"),
    /** Access limited to restricted area rules. */
    RESTRICTED_AREAS("Restricted Areas"),
    /** Access limited to common areas only. */
    COMMON_AREAS_ONLY("Common Areas Only");

    private final String label;

    AccessLevel(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact access level label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a nullable access level label.
     *
     * @param label the optional access level label
     * @return the matching access level, or {@code null} when absent
     * @throws ApiException if the label is blank or unsupported
     */
    public static AccessLevel fromNullableLabel(String label) {
        if (label == null) {
            return null;
        }
        if (label.isBlank()) {
            throw new ApiException(SecurityErrorCode.ACCESS_LEVEL_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(SecurityErrorCode.ACCESS_LEVEL_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated access level labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(AccessLevel::label).collect(Collectors.joining(", "));
    }
}
