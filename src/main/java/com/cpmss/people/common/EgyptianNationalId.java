package com.cpmss.people.common;

import com.cpmss.platform.exception.ApiException;

import java.util.regex.Pattern;

/**
 * Egyptian 14-digit national identifier.
 *
 * <p>The value is optional for non-Egyptian persons, but required when the
 * person's nationality is Egyptian. That nationality-dependent requirement is
 * represented by {@link #forNationality(String, String)}.
 *
 * @param value the 14-digit Egyptian national ID
 */
public record EgyptianNationalId(String value) {

    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^\\d{14}$");

    /**
     * Creates an Egyptian national ID.
     *
     * @param value the raw national ID
     * @throws ApiException if the value is missing or is not 14 digits
     */
    public EgyptianNationalId {
        if (value == null || value.isBlank()) {
            throw new ApiException(PeopleErrorCode.NATIONAL_ID_REQUIRED);
        }

        value = value.strip();
        if (!NATIONAL_ID_PATTERN.matcher(value).matches()) {
            throw new ApiException(PeopleErrorCode.NATIONAL_ID_INVALID);
        }
    }

    /**
     * Creates an Egyptian national ID.
     *
     * @param value the raw national ID
     * @return the validated national ID
     * @throws ApiException if the value is missing or is not 14 digits
     */
    public static EgyptianNationalId of(String value) {
        return new EgyptianNationalId(value);
    }

    /**
     * Creates an optional Egyptian national ID.
     *
     * @param value the optional raw national ID
     * @return the validated national ID, or {@code null} when absent
     * @throws ApiException if the value is present but invalid
     */
    public static EgyptianNationalId nullable(String value) {
        return value == null || value.isBlank() ? null : new EgyptianNationalId(value);
    }

    /**
     * Applies the nationality-dependent national ID rule.
     *
     * @param nationality the person's nationality
     * @param value the optional raw national ID
     * @return the validated national ID, or {@code null} when not required
     * @throws ApiException if an Egyptian person has no valid national ID
     */
    public static EgyptianNationalId forNationality(String nationality, String value) {
        if ("Egyptian".equalsIgnoreCase(nationality)) {
            return new EgyptianNationalId(value);
        }
        return nullable(value);
    }
}
