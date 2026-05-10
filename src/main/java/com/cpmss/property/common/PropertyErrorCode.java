package com.cpmss.property.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the property bounded context.
 *
 * <p>Covers property measurements, building types, facility management,
 * unit status, operating hours, facility and unit rules and services.
 *
 * @see ErrorCode
 */
public enum PropertyErrorCode implements ErrorCode {

    /** Area is missing. */
    AREA_REQUIRED(422, "Area is required"),

    /** Area is not positive. */
    AREA_NOT_POSITIVE(422, "Area must be positive"),

    /** Count is missing. */
    COUNT_REQUIRED(422, "Count is required"),

    /** Count is negative. */
    COUNT_NEGATIVE(422, "Count cannot be negative"),

    /** Building type is missing. */
    BUILDING_TYPE_REQUIRED(422, "Building type is required"),

    /** Building type label is unsupported. */
    BUILDING_TYPE_INVALID(422, "Building type is not allowed"),

    /** Facility management type is missing. */
    FACILITY_MGMT_TYPE_REQUIRED(422, "Facility management type is required"),

    /** Facility management type label is unsupported. */
    FACILITY_MGMT_TYPE_INVALID(422, "Facility management type is not allowed"),

    /** Unit status is missing. */
    UNIT_STATUS_REQUIRED(422, "Unit status is required"),

    /** Unit status label is unsupported. */
    UNIT_STATUS_INVALID(422, "Unit status is not allowed"),

    /** Operating hours are incomplete. */
    OPERATING_HOURS_INCOMPLETE(422, "Opening and closing time must be set together"),

    /** Closing time is not after opening time. */
    OPERATING_HOURS_INVALID(422, "Closing time must be after opening time"),

    /** Facility management type must match compound type. */
    FACILITY_TYPE_MISMATCH(422, "Facility management type does not match compound type"),

    /** Facility not found. */
    FACILITY_NOT_FOUND(404, "Facility not found"),

    /** Unit not found. */
    UNIT_NOT_FOUND(404, "Unit not found"),

    /** Building not found. */
    BUILDING_NOT_FOUND(404, "Building not found"),

    /** Compound not found. */
    COMPOUND_NOT_FOUND(404, "Compound not found"),

    /** Unit number already exists in this facility. */
    UNIT_DUPLICATE(409, "Unit number already exists in this facility"),

    /** Current facility manager assignment not found. */
    FACILITY_MANAGER_NOT_FOUND(404, "Facility manager assignment not found"),

    /** Facility management type mismatch with compound. */
    FACILITY_MGMT_MISMATCH_RESIDENTIAL(422, "Residential compounds require self management"),

    /** Commercial compound requires company management. */
    FACILITY_MGMT_MISMATCH_COMMERCIAL(422, "Commercial compounds require company management"),

    /** User cannot access this property record. */
    PROPERTY_RECORD_ACCESS_DENIED(403, "Property record access denied");

    private final int status;
    private final String defaultMessage;

    PropertyErrorCode(int status, String defaultMessage) {
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
