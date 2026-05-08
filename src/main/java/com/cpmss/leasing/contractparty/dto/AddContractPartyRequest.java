package com.cpmss.leasing.contractparty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload for adding a party to a contract.
 *
 * <p>Valid roles: Primary Signer, Guarantor, Emergency Contact,
 * Corporate Representative, Authorizing Staff. Each contract
 * may have at most one Primary Signer.
 *
 * @param personId   the person joining the contract
 * @param role       the party's role in the contract
 * @param dateSigned when the party signed (null = not yet signed)
 */
public record AddContractPartyRequest(
        @NotNull UUID personId,
        @NotBlank String role,
        Instant dateSigned
) {}
