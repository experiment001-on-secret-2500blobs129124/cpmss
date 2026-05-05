package com.cpmss.gateguardassignment.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a gate guard assignment.
 */
public record GateGuardAssignmentResponse(
        UUID id, UUID guardId, UUID gateId, UUID taskAssignmentId,
        UUID shiftTypeId, Instant shiftStart, Instant shiftEnd,
        Instant createdAt, Instant updatedAt
) {}
