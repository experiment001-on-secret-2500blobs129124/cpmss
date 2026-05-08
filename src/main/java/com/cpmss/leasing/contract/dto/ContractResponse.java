package com.cpmss.leasing.contract.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a contract.
 *
 * <p>Includes target IDs — exactly one of unitId or facilityId
 * will be non-null.
 *
 * @param id                    the contract's UUID primary key
 * @param contractReference     human-readable document ID
 * @param startDate             contract start date
 * @param endDate               contract end date (may be {@code null})
 * @param contractType          contract type (Residential or Commercial)
 * @param contractStatus        lifecycle status
 * @param paymentFrequency      payment frequency (may be {@code null})
 * @param finalPrice            agreed final price (may be {@code null})
 * @param securityDepositAmount security deposit amount (may be {@code null})
 * @param renewalTerms          free-text renewal terms (may be {@code null})
 * @param unitId                unit target UUID (may be {@code null})
 * @param facilityId            facility target UUID (may be {@code null})
 * @param createdAt             when the contract was created
 * @param updatedAt             when the contract was last modified
 */
public record ContractResponse(
        UUID id,
        String contractReference,
        LocalDate startDate,
        LocalDate endDate,
        String contractType,
        String contractStatus,
        String paymentFrequency,
        BigDecimal finalPrice,
        BigDecimal securityDepositAmount,
        String renewalTerms,
        UUID unitId,
        UUID facilityId,
        Instant createdAt,
        Instant updatedAt
) {}
