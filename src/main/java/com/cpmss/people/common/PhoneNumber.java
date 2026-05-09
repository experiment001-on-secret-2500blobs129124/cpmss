package com.cpmss.people.common;

import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Phone number with country dialing code.
 *
 * <p>Mapped to the existing {@code country_code} and {@code phone} columns
 * when embedded by {@code PersonPhone}. The value object validates only the
 * structural shape guaranteed by the current schema: required values and
 * column lengths.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {

    /** Country dialing code. */
    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode;

    /** Local or national phone number text. */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /**
     * Creates a phone number.
     *
     * @param countryCode the country dialing code
     * @param phone the phone number
     * @throws ApiException if either value is missing or too long
     */
    public PhoneNumber(String countryCode, String phone) {
        this.countryCode = normalize(countryCode, 5, "Phone country code");
        this.phone = normalize(phone, 20, "Phone number");
    }

    private static String normalize(String value, int maxLength, String label) {
        if (value == null || value.isBlank()) {
            throw new ApiException(PeopleErrorCode.PHONE_REQUIRED);
        }

        String normalized = value.strip();
        if (normalized.length() > maxLength) {
            throw new ApiException(PeopleErrorCode.PHONE_TOO_LONG);
        }
        return normalized;
    }
}
