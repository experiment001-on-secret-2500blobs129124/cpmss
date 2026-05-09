package com.cpmss.security.vehicle;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.Locale;

/**
 * Vehicle license plate value stored in vehicle and manual-entry columns.
 *
 * <p>The value is normalized by trimming surrounding whitespace and converting
 * Latin letters to uppercase. Repository-backed uniqueness remains in
 * {@link VehicleRules}; this type only owns scalar validation.
 */
public record LicensePlate(String value) {

    /**
     * Creates a normalized license plate value.
     *
     * @throws ApiException if the plate is missing, blank, or longer than
     *                           the database column limit
     */
    public LicensePlate {
        if (value == null || value.isBlank()) {
            throw new ApiException(SecurityErrorCode.LICENSE_PLATE_REQUIRED);
        }
        value = value.strip().toUpperCase(Locale.ROOT);
        if (value.length() > 20) {
            throw new ApiException(SecurityErrorCode.LICENSE_PLATE_TOO_LONG);
        }
    }

    /**
     * Creates a normalized license plate value.
     *
     * @param value the raw license plate string
     * @return the normalized license plate
     * @throws ApiException if the plate is invalid
     */
    public static LicensePlate of(String value) {
        return new LicensePlate(value);
    }

    /**
     * Creates a nullable normalized license plate value.
     *
     * @param value the optional raw license plate string
     * @return the normalized plate, or {@code null} when absent
     * @throws ApiException if the plate is present but invalid
     */
    public static LicensePlate ofNullable(String value) {
        return value != null ? of(value) : null;
    }
}
