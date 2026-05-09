package com.cpmss.security.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the security bounded context.
 *
 * <p>Covers access permits, gates, vehicles, license plates, gate entries,
 * and permit validity rules.
 *
 * @see ErrorCode
 */
public enum SecurityErrorCode implements ErrorCode {

    // --- Access Permit ---

    /** Access level is missing. */
    ACCESS_LEVEL_REQUIRED(422, "Access level cannot be blank"),

    /** Access level label is unsupported. */
    ACCESS_LEVEL_INVALID(422, "Access level is not allowed"),

    /** Permit status is missing. */
    PERMIT_STATUS_REQUIRED(422, "Permit status is required"),

    /** Permit status label is unsupported. */
    PERMIT_STATUS_INVALID(422, "Permit status is not allowed"),

    /** Permit type is missing. */
    PERMIT_TYPE_REQUIRED(422, "Permit type is required"),

    /** Permit type label is unsupported. */
    PERMIT_TYPE_INVALID(422, "Permit type is not allowed"),

    /** Permit issue date is missing. */
    PERMIT_ISSUE_DATE_REQUIRED(422, "Permit issue date is required"),

    /** Permit expiry is before issue date. */
    PERMIT_DATE_RANGE_INVALID(422, "Permit expiry date cannot be before issue date"),

    /** Permit check date is missing. */
    PERMIT_CHECK_DATE_REQUIRED(422, "Permit check date is required"),

    /** Person already has an active permit. */
    PERMIT_ALREADY_ACTIVE(409, "Person already has an active access permit"),

    // --- Gate ---

    /** Gate number is already in use. */
    GATE_DUPLICATE(409, "Gate number is already in use"),

    /** Gate status is missing. */
    GATE_STATUS_REQUIRED(422, "Gate status cannot be blank"),

    /** Gate status label is unsupported. */
    GATE_STATUS_INVALID(422, "Gate status is not allowed"),

    /** Gate direction is missing. */
    GATE_DIRECTION_REQUIRED(422, "Gate direction is required"),

    /** Gate direction label is unsupported. */
    GATE_DIRECTION_INVALID(422, "Gate direction is not allowed"),

    // --- Gate Entry ---

    /** Guard is not assigned to the gate. */
    GUARD_NOT_ASSIGNED(403, "Guard is not assigned to this gate"),

    /** User cannot access this security record. */
    SECURITY_RECORD_ACCESS_DENIED(403, "Security record access denied"),

    /** Gate entry record not found. */
    GATE_ENTRY_NOT_FOUND(404, "Gate entry not found"),

    /** Gate guard assignment not found. */
    GATE_GUARD_ASSIGNMENT_NOT_FOUND(404, "Gate guard assignment not found"),

    // --- Vehicle ---

    /** License plate is missing. */
    LICENSE_PLATE_REQUIRED(422, "License plate is required"),

    /** License plate exceeds maximum length. */
    LICENSE_PLATE_TOO_LONG(422, "License plate cannot exceed 20 characters"),

    /** Vehicle must have exactly one owner. */
    VEHICLE_OWNER_INVALID(422, "Vehicle must have exactly one owner"),

    /** License number is already registered. */
    VEHICLE_LICENSE_DUPLICATE(409, "License number is already registered"),

    /** Vehicle permit is already linked. */
    VEHICLE_PERMIT_ALREADY_LINKED(409, "Vehicle permit is already linked"),

    /** Vehicle permit link does not exist. */
    VEHICLE_PERMIT_NOT_LINKED(404, "Vehicle permit link does not exist"),

    /** Vehicle can only be linked to vehicle sticker permits. */
    VEHICLE_PERMIT_TYPE_INVALID(422, "Vehicle can only be linked to vehicle sticker permits"),

    /** Vehicle can only be linked to active permits. */
    VEHICLE_PERMIT_NOT_ACTIVE(422, "Vehicle can only be linked to active permits"),

    /** Access permit not found. */
    ACCESS_PERMIT_NOT_FOUND(404, "Access permit not found"),

    /** Gate not found. */
    GATE_NOT_FOUND(404, "Gate not found"),

    /** Vehicle not found. */
    VEHICLE_NOT_FOUND(404, "Vehicle not found");

    private final int status;
    private final String defaultMessage;

    SecurityErrorCode(int status, String defaultMessage) {
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
