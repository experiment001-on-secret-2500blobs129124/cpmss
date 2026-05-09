package com.cpmss.identity.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the identity bounded context.
 *
 * <p>Covers authentication, user registration, and login rules.
 *
 * @see ErrorCode
 */
public enum IdentityErrorCode implements ErrorCode {

    /** Login email is already registered. */
    EMAIL_DUPLICATE(409, "Email is already registered"),

    /** Login credentials are invalid. */
    CREDENTIALS_INVALID(401, "Invalid email or password"),

    /** User account not found. */
    USER_NOT_FOUND(404, "User not found"),

    /** Setup already completed. */
    SETUP_ALREADY_DONE(409, "Initial setup has already been completed"),

    /** Role not found during registration. */
    ROLE_NOT_FOUND(404, "Role not found"),

    /** Person not found during registration. */
    PERSON_NOT_FOUND(404, "Person not found"),

    /** Password is required. */
    PASSWORD_REQUIRED(422, "Password is required"),

    /** Registration data is invalid. */
    REGISTRATION_INVALID(422, "Registration data is invalid"),

    /** Token is invalid or expired. */
    TOKEN_INVALID(401, "Token is invalid or expired"),

    /** Account is disabled. */
    ACCOUNT_DISABLED(403, "Account is deactivated"),

    /** Cannot change your own system role. */
    SELF_ROLE_CHANGE_FORBIDDEN(403, "Cannot change your own system role"),

    /** Cannot deactivate your own account. */
    SELF_DEACTIVATION_FORBIDDEN(403, "Cannot deactivate your own account"),

    /** Insufficient authority for role assignment. */
    AUTHORITY_INSUFFICIENT(403, "Insufficient authority for role assignment"),

    /** Department manager creation scope violation. */
    DEPT_MANAGER_SCOPE(403, "Department managers can only create STAFF and GATE_GUARD accounts"),

    /** No authenticated user in security context. */
    NOT_AUTHENTICATED(403, "No authenticated user"),

    /** No role found in security context. */
    NO_ROLE_IN_CONTEXT(403, "No role found in security context"),

    /** User cannot access this account record. */
    IDENTITY_RECORD_ACCESS_DENIED(403, "Identity record access denied");

    private final int status;
    private final String defaultMessage;

    IdentityErrorCode(int status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() { return name(); }

    @Override
    public int status() { return status; }

    @Override
    public String defaultMessage() { return defaultMessage; }
}
