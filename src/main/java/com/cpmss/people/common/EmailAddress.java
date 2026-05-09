package com.cpmss.people.common;

import com.cpmss.platform.exception.ApiException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Normalized email address used by login and person contact data.
 *
 * <p>AppUser email is the login credential, while Person email is contact
 * information. Both still share the same low-level format and normalization
 * rules. Database columns remain plain varchar columns.
 *
 * @param value the normalized lowercase email address
 */
public record EmailAddress(String value) {

    /** Maximum email length supported by the App_User login column. */
    public static final int MAX_LOGIN_LENGTH = 255;

    /** Maximum email length supported by the Person_Email contact column. */
    public static final int MAX_CONTACT_LENGTH = 150;

    private static final Pattern BASIC_EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /**
     * Creates an email address with the login-column length limit.
     *
     * @param value the raw email address
     * @throws ApiException if the email is missing, too long, or invalid
     */
    public EmailAddress {
        value = normalize(value, MAX_LOGIN_LENGTH);
    }

    /**
     * Creates an email address for login usage.
     *
     * @param value the raw email address
     * @return the normalized email address
     * @throws ApiException if the email is missing, too long, or invalid
     */
    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }

    /**
     * Creates an email address for the Person_Email contact table.
     *
     * @param value the raw email address
     * @return the normalized contact email address
     * @throws ApiException if the email is missing, too long, or invalid
     */
    public static EmailAddress contact(String value) {
        return new EmailAddress(normalize(value, MAX_CONTACT_LENGTH));
    }

    private static String normalize(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new ApiException(PeopleErrorCode.EMAIL_REQUIRED);
        }

        String normalized = value.strip().toLowerCase(Locale.ROOT);
        if (normalized.length() > maxLength) {
            throw new ApiException(PeopleErrorCode.EMAIL_TOO_LONG);
        }
        if (!BASIC_EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ApiException(PeopleErrorCode.EMAIL_INVALID);
        }
        return normalized;
    }
}
