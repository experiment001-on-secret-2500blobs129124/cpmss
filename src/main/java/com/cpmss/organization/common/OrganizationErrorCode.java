package com.cpmss.organization.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the organization bounded context.
 *
 * <p>Covers departments, supervision rules, and compound management.
 *
 * @see ErrorCode
 */
public enum OrganizationErrorCode implements ErrorCode {

    /** Department name already exists. */
    DEPARTMENT_DUPLICATE(409, "Department already exists"),

    /** A person cannot supervise themselves. */
    SELF_SUPERVISION_FORBIDDEN(422, "A person cannot supervise themselves"),

    /** Department not found. */
    DEPARTMENT_NOT_FOUND(404, "Department not found"),

    /** Compound not found. */
    COMPOUND_NOT_FOUND(404, "Compound not found");

    private final int status;
    private final String defaultMessage;

    OrganizationErrorCode(int status, String defaultMessage) {
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
