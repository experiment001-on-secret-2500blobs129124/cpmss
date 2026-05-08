package com.cpmss.leasing.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a contract.
 *
 * <p>Exactly one of {@code unitId} or {@code facilityId} must be
 * set — enforced by {@link com.cpmss.leasing.contract.ContractRules}.
 *
 * @param contractReference    human-readable document ID (system-wide unique)
 * @param startDate            contract start date
 * @param endDate              contract end date ({@code null} = open-ended)
 * @param contractType         contract type (Residential or Commercial)
 * @param contractStatus       lifecycle status (Draft, Active, etc.)
 * @param paymentFrequency     payment frequency (Monthly, Quarterly, etc.)
 * @param finalPrice           agreed final price
 * @param securityDepositAmount security deposit amount
 * @param renewalTerms         free-text renewal terms
 * @param unitId               unit target UUID (may be {@code null})
 * @param facilityId           facility target UUID (may be {@code null})
 */
public record CreateContractRequest(
        @NotBlank @Size(max = 50) String contractReference,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotBlank @Size(max = 50) String contractType,
        @NotBlank @Size(max = 50) String contractStatus,
        @Size(max = 50) String paymentFrequency,
        BigDecimal finalPrice,
        BigDecimal securityDepositAmount,
        String renewalTerms,
        UUID unitId,
        UUID facilityId
) {}
