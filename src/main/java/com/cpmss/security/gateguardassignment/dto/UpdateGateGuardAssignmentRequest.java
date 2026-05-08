package com.cpmss.security.gateguardassignment.dto;

import java.time.Instant;

/**
 * Request payload for updating a gate guard assignment (ending a shift).
 *
 * @param shiftEnd shift end timestamp
 */
public record UpdateGateGuardAssignmentRequest(
        Instant shiftEnd
) {}
