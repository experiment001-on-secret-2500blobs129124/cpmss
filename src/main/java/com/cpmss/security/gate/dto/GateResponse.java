package com.cpmss.security.gate.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a gate.
 *
 * <p>Includes the owning compound's ID and name (denormalized
 * for display convenience).
 *
 * @param id           the gate's UUID primary key
 * @param gateNo       the gate number
 * @param gateName     display name of the gate
 * @param gateType     classification (may be {@code null})
 * @param gateStatus   operational status (Active, Under Maintenance, Closed;
 *                     may be {@code null})
 * @param compoundId   the owning compound's UUID
 * @param compoundName the owning compound's display name
 * @param createdAt    when the gate was created
 * @param updatedAt    when the gate was last modified
 */
public record GateResponse(
        UUID id,
        String gateNo,
        String gateName,
        String gateType,
        String gateStatus,
        UUID compoundId,
        String compoundName,
        Instant createdAt,
        Instant updatedAt
) {}
