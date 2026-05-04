package com.cpmss.auth;

/**
 * System-level permission roles for software access.
 *
 * <p>Independent from business roles ({@code Person_Role}).
 * See DATABASE.md § "Role Architecture".
 */
public enum SystemRole {
    /** Full system access. */
    ADMIN,
    /** Department-level access — approvals, reports. */
    MANAGER,
    /** Operational access — daily tasks, attendance. */
    STAFF
}
