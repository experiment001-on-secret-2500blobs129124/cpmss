package com.cpmss.people.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the people bounded context.
 *
 * <p>Covers identity value validation (email, phone, passport, national ID,
 * gender), person role rules, and uniqueness constraints for people, roles,
 * and qualifications.
 *
 * @see ErrorCode
 */
public enum PeopleErrorCode implements ErrorCode {

    // --- Identity Values ---

    /** Email address is missing. */
    EMAIL_REQUIRED(422, "Email address is required"),

    /** Email address exceeds maximum length. */
    EMAIL_TOO_LONG(422, "Email address is too long"),

    /** Email address format is invalid. */
    EMAIL_INVALID(422, "Email address is invalid"),

    /** Phone number is missing. */
    PHONE_REQUIRED(422, "Phone number is required"),

    /** Phone number exceeds maximum length. */
    PHONE_TOO_LONG(422, "Phone number is too long"),

    /** Passport number is missing. */
    PASSPORT_REQUIRED(422, "Passport number is required"),

    /** Passport number exceeds maximum length. */
    PASSPORT_TOO_LONG(422, "Passport number is too long"),

    /** Egyptian national ID is missing. */
    NATIONAL_ID_REQUIRED(422, "Egyptian national ID is required"),

    /** Egyptian national ID format is invalid. */
    NATIONAL_ID_INVALID(422, "Egyptian national ID must be 14 digits"),

    /** Gender value is missing. */
    GENDER_REQUIRED(422, "Gender is required"),

    /** Gender label is unsupported. */
    GENDER_INVALID(422, "Gender is not allowed"),

    // --- Person Rules ---

    /** A person must have at least one assigned role. */
    PERSON_ROLE_REQUIRED(422, "A person must have at least one role"),

    /** Passport number is already registered to another person. */
    PASSPORT_DUPLICATE(409, "Passport number is already registered"),

    /** Person not found. */
    PERSON_NOT_FOUND(404, "Person not found"),

    /** Role not found. */
    ROLE_NOT_FOUND(404, "Role not found"),

    /** Qualification not found. */
    QUALIFICATION_NOT_FOUND(404, "Qualification not found"),

    // --- Qualification Rules ---

    /** Qualification name already exists. */
    QUALIFICATION_DUPLICATE(409, "Qualification already exists"),

    // --- Role Rules ---

    /** Role name already exists. */
    ROLE_DUPLICATE(409, "Role already exists");

    private final int status;
    private final String defaultMessage;

    PeopleErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
