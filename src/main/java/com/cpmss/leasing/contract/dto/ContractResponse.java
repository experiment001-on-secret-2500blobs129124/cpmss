package com.cpmss.leasing.contract.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.ContractPeriod;
import com.cpmss.leasing.common.ContractStatus;
import com.cpmss.leasing.common.ContractType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a contract.
 *
 * <p>Includes target IDs — exactly one of unitId or facilityId
 * will be non-null.
 *
 * @param id                    the contract's UUID primary key
 * @param contractReference     human-readable document ID
 * @param period                contract start/end date period
 * @param contractType          contract type (Residential or Commercial)
 * @param contractStatus        lifecycle status
 * @param paymentFrequency      payment frequency (may be {@code null})
 * @param finalPrice            agreed final price money (may be {@code null})
 * @param securityDeposit       security deposit money (may be {@code null})
 * @param renewalTerms          free-text renewal terms (may be {@code null})
 * @param unitId                unit target UUID (may be {@code null})
 * @param facilityId            facility target UUID (may be {@code null})
 * @param createdAt             when the contract was created
 * @param updatedAt             when the contract was last modified
 */
public record ContractResponse(
        UUID id,
        String contractReference,
        ContractPeriod period,
        ContractType contractType,
        ContractStatus contractStatus,
        String paymentFrequency,
        Money finalPrice,
        Money securityDeposit,
        String renewalTerms,
        UUID unitId,
        UUID facilityId,
        Instant createdAt,
        Instant updatedAt
) {}
