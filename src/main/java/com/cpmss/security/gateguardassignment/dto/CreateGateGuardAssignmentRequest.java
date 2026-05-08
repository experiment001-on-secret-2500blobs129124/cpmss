package com.cpmss.security.gateguardassignment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload for creating a gate guard assignment.
 *
 * @param guardId           the guard UUID
 * @param gateId            the gate UUID
 * @param taskAssignmentId  the authorizing task assignment UUID
 * @param shiftTypeId       the shift type UUID (optional)
 * @param shiftStart        shift start timestamp
 * @param shiftEnd          shift end timestamp ({@code null} = still on duty)
 */
public record CreateGateGuardAssignmentRequest(
        @NotNull UUID guardId,
        @NotNull UUID gateId,
        @NotNull UUID taskAssignmentId,
        UUID shiftTypeId,
        @NotNull Instant shiftStart,
        Instant shiftEnd
) {}
