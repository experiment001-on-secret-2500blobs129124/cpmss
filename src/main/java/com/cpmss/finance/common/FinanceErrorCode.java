package com.cpmss.finance.common;

import com.cpmss.platform.exception.ErrorCode;

/**
 * Error codes for the finance bounded context.
 *
 * <p>Covers money value validation, payment vocabulary, payment
 * reference and number formats, bank account IBAN/SWIFT validation,
 * and payroll payment rules.
 *
 * @see ErrorCode
 */
public enum FinanceErrorCode implements ErrorCode {

    // --- Money ---

    /** Money amount is missing. */
    MONEY_AMOUNT_REQUIRED(422, "Money amount is required"),

    /** Money amount is below zero. */
    MONEY_AMOUNT_NEGATIVE(422, "Money amount cannot be negative"),

    /** Positive money is required. */
    MONEY_AMOUNT_NOT_POSITIVE(422, "Money amount must be positive"),

    /** Currency is missing. */
    MONEY_CURRENCY_REQUIRED(422, "Money currency is required"),

    /** Currency is not a valid ISO-4217 code. */
    MONEY_CURRENCY_INVALID(422, "Money currency must be a valid ISO-4217 code"),

    /** Currency-aware arithmetic mixes different currencies. */
    MONEY_CURRENCY_MISMATCH(422, "Cannot add money with different currencies"),

    /** Money to add is required. */
    MONEY_ADD_REQUIRED(422, "Money to add is required"),

    // --- Payment Vocabulary ---

    /** Payment type is missing. */
    PAYMENT_TYPE_REQUIRED(422, "Payment type is required"),

    /** Payment type is not an allowed value. */
    PAYMENT_TYPE_INVALID(422, "Payment type is not allowed"),

    /** Payment direction is missing. */
    PAYMENT_DIRECTION_REQUIRED(422, "Payment direction is required"),

    /** Payment direction is not an allowed value. */
    PAYMENT_DIRECTION_INVALID(422, "Payment direction is not allowed"),

    /** Payment method is missing. */
    PAYMENT_METHOD_REQUIRED(422, "Payment method cannot be blank"),

    /** Payment method is not an allowed value. */
    PAYMENT_METHOD_INVALID(422, "Payment method is not allowed"),

    /** Reconciliation status is missing. */
    PAYMENT_RECONCILIATION_STATUS_REQUIRED(422, "Reconciliation status is required"),

    /** Reconciliation status is not an allowed value. */
    PAYMENT_RECONCILIATION_STATUS_INVALID(422, "Reconciliation status is not allowed"),

    // --- Payment Number And Reference ---

    /** Payment number is required. */
    PAYMENT_NUMBER_REQUIRED(422, "Payment number is required"),

    /** Payment number format or length is invalid. */
    PAYMENT_NUMBER_INVALID(422, "Payment number format is invalid"),

    /** Payment number exceeds maximum length. */
    PAYMENT_NUMBER_TOO_LONG(422, "Payment number must be at most 20 characters"),

    /** Payment reference is required for a required-reference path. */
    PAYMENT_REFERENCE_REQUIRED(422, "Payment reference is required"),

    /** Payment reference format or length is invalid. */
    PAYMENT_REFERENCE_INVALID(422, "Payment reference must be at most 100 characters"),

    /** Payment amount does not match the required subtype amount. */
    PAYMENT_AMOUNT_INVALID(422, "Payment amount does not match required amount"),

    // --- Bank Account ---

    /** Bank account has zero or multiple owners. */
    BANK_ACCOUNT_OWNER_INVALID(422, "Bank account must have exactly one owner"),

    /** IBAN is required for a required-IBAN path. */
    BANK_IBAN_REQUIRED(422, "IBAN is required"),

    /** IBAN length is invalid. */
    BANK_IBAN_TOO_LONG(422, "IBAN must be at most 34 characters"),

    /** IBAN format is invalid. */
    BANK_IBAN_FORMAT_INVALID(422, "IBAN format is invalid"),

    /** IBAN checksum is invalid. */
    BANK_IBAN_CHECKSUM_INVALID(422, "IBAN checksum is invalid"),

    /** SWIFT/BIC code is required for a required-SWIFT path. */
    BANK_SWIFT_REQUIRED(422, "SWIFT/BIC code is required"),

    /** SWIFT/BIC code format is invalid. */
    BANK_SWIFT_INVALID(422, "SWIFT/BIC code format is invalid"),

    /** Bank account not found. */
    BANK_ACCOUNT_NOT_FOUND(404, "Bank account not found"),

    /** Payment direction does not match the requested payment subtype. */
    PAYMENT_DIRECTION_MISMATCH(422, "Payment direction does not match payment subtype"),

    /** Payment subtype child row already exists. */
    PAYMENT_DETAIL_DUPLICATE(409, "Payment detail already exists for this payment"),

    /** Payroll close record is required before paying payroll. */
    PAYROLL_RECORD_NOT_FOUND(404, "Payroll close record not found"),

    /** Work order must be completed before it can be paid. */
    WORK_ORDER_NOT_PAYABLE(422, "Work order must be completed before payment"),

    /** Payment not found. */
    PAYMENT_NOT_FOUND(404, "Payment not found"),

    // --- Payroll ---

    /** Payroll period is missing. */
    PAYROLL_PERIOD_REQUIRED(422, "Payroll period is required"),

    /** User cannot access this finance record. */
    FINANCE_RECORD_ACCESS_DENIED(403, "Finance record access denied");

    private final int status;
    private final String defaultMessage;

    FinanceErrorCode(int status, String defaultMessage) {
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
