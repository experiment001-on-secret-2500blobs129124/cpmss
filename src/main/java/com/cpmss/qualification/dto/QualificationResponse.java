package com.cpmss.qualification.dto;
import java.time.Instant;
import java.util.UUID;
/**
 * Response payload for a qualification.
 *
 * @param id                the qualification UUID
 * @param qualificationName the qualification's name
 * @param createdAt         when the qualification was created
 * @param updatedAt         when the qualification was last modified
 */
public record QualificationResponse(
        UUID id,
        String qualificationName,
        Instant createdAt,
        Instant updatedAt
) {}
