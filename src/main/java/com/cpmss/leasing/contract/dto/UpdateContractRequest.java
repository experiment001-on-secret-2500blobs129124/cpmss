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
 * Request payload for updating an existing contract.
 *
 * <p>All fields are replaceable. Exactly one target must be set.
 * Contracts are never deleted — closed by status change.
 *
 * @param contractReference    human-readable document ID
 * @param period               contract start/end date period
 * @param contractType         contract type
 * @param contractStatus       lifecycle status
 * @param paymentFrequency     payment frequency
 * @param finalPrice           optional agreed final price money
 * @param securityDeposit      optional security deposit money
 * @param renewalTerms         free-text renewal terms
 * @param unitId               unit target UUID (may be {@code null})
 * @param facilityId           facility target UUID (may be {@code null})
 */
public record UpdateContractRequest(
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
