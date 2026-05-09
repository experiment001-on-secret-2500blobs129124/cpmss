package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Facility management model stored in {@code Facility.management_type}.
 *
 * <p>Vendor-vs-compound FK consistency remains a service/database rule; this
 * enum only owns the finite vocabulary.
 */
public enum FacilityManagementType {
    /** Facility is managed directly by the compound. */
    COMPOUND("Compound"),
    /** Facility is managed by an external vendor company. */
    VENDOR("Vendor");

    private final String label;

    FacilityManagementType(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in the database
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required facility management type label.
     *
     * @param label the management type label
     * @return the matching type
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static FacilityManagementType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(PropertyErrorCode.FACILITY_MGMT_TYPE_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(PropertyErrorCode.FACILITY_MGMT_TYPE_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(FacilityManagementType::label)
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
