package com.cpmss.compound.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a compound.
 *
 * <p>Exposes all user-visible compound fields plus audit timestamps.
 *
 * @param id           the compound's UUID primary key
 * @param compoundName display name of the compound
 * @param country      country where the compound is located
 * @param city         city where the compound is located
 * @param district     district or neighbourhood (may be {@code null})
 * @param createdAt    when the compound was created
 * @param updatedAt    when the compound was last modified
 */
public record CompoundResponse(
        UUID id,
        String compoundName,
        String country,
        String city,
        String district,
        Instant createdAt,
        Instant updatedAt
) {}
