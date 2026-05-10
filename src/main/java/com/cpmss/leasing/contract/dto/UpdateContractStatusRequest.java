package com.cpmss.leasing.contract.dto;

import com.cpmss.leasing.common.ContractStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for transitioning a contract lifecycle status.
 *
 * @param contractStatus the requested next status
 */
public record UpdateContractStatusRequest(
        @NotNull ContractStatus contractStatus
) {}
