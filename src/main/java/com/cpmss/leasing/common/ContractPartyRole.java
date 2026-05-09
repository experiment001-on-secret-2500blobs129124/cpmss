package com.cpmss.leasing.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Contract party role stored in {@code Contract_Party.role}.
 *
 * <p>Labels must match the Flyway V2 {@code chk_contract_party_role}
 * constraint because the role is also part of the composite primary key.
 */
public enum ContractPartyRole {
    /** Sole primary signer for a contract. */
    PRIMARY_SIGNER("Primary Signer"),
    /** Person guaranteeing the contract. */
    GUARANTOR("Guarantor"),
    /** Emergency contact for a contract party or resident. */
    EMERGENCY_CONTACT("Emergency Contact"),
    /** Representative signing for a corporate tenant. */
    CORPORATE_REPRESENTATIVE("Corporate Representative"),
    /** Staff member authorizing or processing the contract. */
    AUTHORIZING_STAFF("Authorizing Staff");

    private final String label;

    ContractPartyRole(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact label stored in {@code role}
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required party role label.
     *
     * @param label the party role label
     * @return the matching role
     * @throws ApiException if the label is missing or unsupported
     */
    @JsonCreator
    public static ContractPartyRole fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(LeasingErrorCode.CONTRACT_PARTY_ROLE_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(LeasingErrorCode.CONTRACT_PARTY_ROLE_INVALID));
    }

    /**
     * Lists labels accepted by the database constraint.
     *
     * @return comma-separated party role labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values())
                .map(ContractPartyRole::label)
                .collect(Collectors.joining(", "));
    }

    /**
     * Serializes this role as its API/database label.
     *
     * @return the exact label stored in the database
     */
    @JsonValue
    public String toJson() {
        return label;
    }
}
