package com.cpmss.property.building.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a building.
 *
 * <p>Includes the owning compound's ID and name (denormalized
 * for display convenience).
 *
 * @param id               the building's UUID primary key
 * @param buildingNo       the building number
 * @param buildingName     display name (may be {@code null})
 * @param buildingType     classification (may be {@code null})
 * @param floorsCount      number of floors (may be {@code null})
 * @param constructionDate construction completion date (may be {@code null})
 * @param compoundId       the owning compound's UUID
 * @param compoundName     the owning compound's display name
 * @param createdAt        when the building was created
 * @param updatedAt        when the building was last modified
 */
public record BuildingResponse(
        UUID id,
        String buildingNo,
        String buildingName,
        String buildingType,
        Integer floorsCount,
        LocalDate constructionDate,
        UUID compoundId,
        String compoundName,
        Instant createdAt,
        Instant updatedAt
) {}
