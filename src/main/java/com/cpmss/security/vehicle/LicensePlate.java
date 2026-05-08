package com.cpmss.security.vehicle;

import com.cpmss.platform.exception.BusinessException;

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
     * @throws BusinessException if the plate is missing, blank, or longer than
     *                           the database column limit
     */
    public LicensePlate {
        if (value == null || value.isBlank()) {
            throw new BusinessException("License plate is required");
        }
        value = value.strip().toUpperCase(Locale.ROOT);
        if (value.length() > 20) {
            throw new BusinessException("License plate cannot exceed 20 characters");
        }
    }

    /**
     * Creates a normalized license plate value.
     *
     * @param value the raw license plate string
     * @return the normalized license plate
     * @throws BusinessException if the plate is invalid
     */
    public static LicensePlate of(String value) {
        return new LicensePlate(value);
    }

    /**
     * Creates a nullable normalized license plate value.
     *
     * @param value the optional raw license plate string
     * @return the normalized plate, or {@code null} when absent
     * @throws BusinessException if the plate is present but invalid
     */
    public static LicensePlate ofNullable(String value) {
        return value != null ? of(value) : null;
    }
}
