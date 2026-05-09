package com.cpmss.people.common;

import com.cpmss.platform.exception.ApiException;

/**
 * Passport number stored in {@code Person.passport_no}.
 *
 * <p>The schema makes passport number required and unique for every person.
 * This value object centralizes the non-empty and length checks while keeping
 * country-specific passport formats out of the model.
 *
 * @param value the normalized passport number
 */
public record PassportNumber(String value) {

    /** Maximum passport length supported by the Person table. */
    public static final int MAX_LENGTH = 20;

    /**
     * Creates a passport number.
     *
     * @param value the raw passport number
     * @throws ApiException if the passport is missing or too long
     */
    public PassportNumber {
        if (value == null || value.isBlank()) {
            throw new ApiException(PeopleErrorCode.PASSPORT_REQUIRED);
        }

        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new ApiException(PeopleErrorCode.PASSPORT_TOO_LONG);
        }
    }

    /**
     * Creates a passport number.
     *
     * @param value the raw passport number
     * @return the validated passport number
     * @throws ApiException if the passport is missing or too long
     */
    public static PassportNumber of(String value) {
        return new PassportNumber(value);
    }
}
