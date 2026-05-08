package com.cpmss.leasing.contract.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.ContractPeriod;
import com.cpmss.leasing.common.ContractStatus;
import com.cpmss.leasing.common.ContractType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a contract.
 *
 * <p>Exactly one of {@code unitId} or {@code facilityId} must be
 * set — enforced by {@link com.cpmss.leasing.contract.ContractRules}.
 *
 * @param contractReference    human-readable document ID (system-wide unique)
 * @param period               contract start/end date period
 * @param contractType         contract type (Residential or Commercial)
 * @param contractStatus       lifecycle status (Draft, Active, etc.)
 * @param paymentFrequency     payment frequency (Monthly, Quarterly, etc.)
 * @param finalPrice           optional agreed final price money
 * @param securityDeposit      optional security deposit money
 * @param renewalTerms         free-text renewal terms
 * @param unitId               unit target UUID (may be {@code null})
 * @param facilityId           facility target UUID (may be {@code null})
 */
public record CreateContractRequest(
        @NotBlank @Size(max = 50) String contractReference,
        @NotNull @Valid ContractPeriod period,
        @NotNull ContractType contractType,
        @NotNull ContractStatus contractStatus,
        @Size(max = 50) String paymentFrequency,
        @Valid Money finalPrice,
        @Valid Money securityDeposit,
        String renewalTerms,
        UUID unitId,
        UUID facilityId
) {}
