package com.cpmss.security.gate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing gate.
 *
 * <p>All fields are replaceable — the entire gate record is
 * overwritten with the values supplied here.
 *
 * @param gateNo     the gate number (system-wide unique)
 * @param gateName   display name of the gate
 * @param gateType   optional classification
 * @param gateStatus optional operational status
 * @param compoundId the owning compound's UUID
 */
public record UpdateGateRequest(
        @NotBlank @Size(max = 20) String gateNo,
        @NotBlank @Size(max = 100) String gateName,
        @Size(max = 50) String gateType,
        @Size(max = 50) String gateStatus,
        @NotNull UUID compoundId
) {}
