package com.cpmss.security.entersat.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a gate access event.
 *
 * @param id               the entry's UUID primary key
 * @param gateId           the gate UUID
 * @param permitId         permit UUID (may be {@code null})
 * @param manualPlateEntry manually entered plate (may be {@code null})
 * @param enteredAt        exact timestamp of the event
 * @param direction        direction (IN or OUT)
 * @param purpose          purpose of the visit (may be {@code null})
 * @param processedById    guard UUID (may be {@code null})
 * @param requestedById    resident/staff UUID (may be {@code null})
 * @param createdAt        when the record was created
 */
public record EntersAtResponse(
        UUID id,
        UUID gateId,
        UUID permitId,
        String manualPlateEntry,
        Instant enteredAt,
        String direction,
        String purpose,
        UUID processedById,
        UUID requestedById,
        Instant createdAt
) {}
