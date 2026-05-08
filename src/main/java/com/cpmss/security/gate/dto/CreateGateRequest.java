package com.cpmss.security.gate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a gate.
 *
 * <p>Gate number and name are required. The compound ID is
 * mandatory — a gate cannot exist without a parent compound.
 *
 * @param gateNo     the gate number (system-wide unique)
 * @param gateName   display name of the gate
 * @param gateType   optional classification (e.g. Pedestrian)
 * @param gateStatus optional operational status
 * @param compoundId the owning compound's UUID
 */
public record CreateGateRequest(
        @NotBlank @Size(max = 20) String gateNo,
        @NotBlank @Size(max = 100) String gateName,
        @Size(max = 50) String gateType,
        @Size(max = 50) String gateStatus,
        @NotNull UUID compoundId
) {}
