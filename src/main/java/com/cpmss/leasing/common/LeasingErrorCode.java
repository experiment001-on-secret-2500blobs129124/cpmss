package com.cpmss.leasing.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the leasing bounded context.
 *
 * <p>Covers contracts, installments, contract parties, residency,
 * and lease lifecycle rules.
 *
 * @see ErrorCode
 */
public enum LeasingErrorCode implements ErrorCode {

    /** Contract type is missing. */
    CONTRACT_TYPE_REQUIRED(422, "Contract type is required"),

    /** Contract type label is unsupported. */
    CONTRACT_TYPE_INVALID(422, "Contract type is not allowed"),

    /** Contract status is missing. */
    CONTRACT_STATUS_REQUIRED(422, "Contract status is required"),

    /** Contract status label is unsupported. */
    CONTRACT_STATUS_INVALID(422, "Contract status is not allowed"),

    /** Installment type is missing. */
    INSTALLMENT_TYPE_REQUIRED(422, "Installment type is required"),

    /** Installment type label is unsupported. */
    INSTALLMENT_TYPE_INVALID(422, "Installment type is not allowed"),

    /** Installment status is missing. */
    INSTALLMENT_STATUS_REQUIRED(422, "Installment status is required"),

    /** Installment status label is unsupported. */
    INSTALLMENT_STATUS_INVALID(422, "Installment status is not allowed"),

    /** Contract party role is missing. */
    CONTRACT_PARTY_ROLE_REQUIRED(422, "Contract party role is required"),

    /** Contract party role label is unsupported. */
    CONTRACT_PARTY_ROLE_INVALID(422, "Contract party role is not allowed"),

    /** Household relationship is missing. */
    HOUSEHOLD_RELATIONSHIP_REQUIRED(422, "Household relationship is required"),

    /** Household relationship label is unsupported. */
    HOUSEHOLD_RELATIONSHIP_INVALID(422, "Household relationship is not allowed"),

    /** Contract period is missing or invalid. */
    CONTRACT_PERIOD_INVALID(422, "Contract period is invalid"),

    /** Residency period is missing or invalid. */
    RESIDENCY_PERIOD_INVALID(422, "Residency period is invalid"),

    /** Contract not found. */
    CONTRACT_NOT_FOUND(404, "Contract not found"),

    /** Installment not found. */
    INSTALLMENT_NOT_FOUND(404, "Installment not found"),

    /** Installment already paid. */
    INSTALLMENT_ALREADY_PAID(409, "Installment is already paid"),

    /** Contract party not found. */
    CONTRACT_PARTY_NOT_FOUND(404, "Contract party not found"),

    /** Residency record not found. */
    RESIDENCY_NOT_FOUND(404, "Residency not found"),

    /** Unit is not available for leasing. */
    UNIT_NOT_AVAILABLE(422, "Unit is not available for leasing"),

    /** Duplicate contract party. */
    CONTRACT_PARTY_DUPLICATE(409, "Person is already a party to this contract"),

    /** Move-in date is missing. */
    MOVE_IN_DATE_REQUIRED(422, "Move-in date is required"),

    /** Move-out date is not after move-in. */
    MOVE_OUT_DATE_INVALID(422, "Move-out date must be after move-in date"),

    /** Contract start date is missing. */
    CONTRACT_START_DATE_REQUIRED(422, "Contract start date is required"),

    /** Contract end date is not after start. */
    CONTRACT_END_DATE_INVALID(422, "Contract end date must be after start date"),

    /** Installment amount is not positive. */
    INSTALLMENT_AMOUNT_NOT_POSITIVE(422, "Installment amount must be positive"),

    /** Installment status transition is invalid. */
    INSTALLMENT_STATUS_TRANSITION_INVALID(422, "Invalid installment status transition"),

    /** Contract status transition is invalid. */
    CONTRACT_STATUS_TRANSITION_INVALID(422, "Invalid contract status transition"),

    /** Contract must cover exactly one target. */
    CONTRACT_TARGET_INVALID(422, "A contract must cover exactly one target (unit or facility)"),

    /** Contract reference is already registered. */
    CONTRACT_REFERENCE_DUPLICATE(409, "Contract reference is already registered"),

    /** Primary signer already exists on contract. */
    PRIMARY_SIGNER_DUPLICATE(409, "Contract already has a Primary Signer"),

    /** Person not found (leasing context). */
    PERSON_NOT_FOUND(404, "Person not found"),

    /** Unit not found (leasing context). */
    UNIT_NOT_FOUND(404, "Unit not found"),

    /** Facility not found (leasing context). */
    FACILITY_NOT_FOUND(404, "Facility not found"),

    /** User cannot access this leasing record. */
    LEASING_RECORD_ACCESS_DENIED(403, "Leasing record access denied");

    private final int status;
    private final String defaultMessage;

    LeasingErrorCode(int status, String defaultMessage) {
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
