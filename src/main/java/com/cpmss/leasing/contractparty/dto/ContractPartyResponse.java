package com.cpmss.leasing.contractparty.dto;

import com.cpmss.leasing.common.ContractPartyRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a contract party record.
 *
 * @param personId   the party's person UUID
 * @param contractId the contract UUID
 * @param role       the party's role in the contract
 * @param dateSigned when the party signed (null = not yet signed)
 */
public record ContractPartyResponse(
        UUID personId,
        UUID contractId,
        ContractPartyRole role,
        Instant dateSigned
) {}
