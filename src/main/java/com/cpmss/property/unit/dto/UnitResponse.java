package com.cpmss.property.unit.dto;

import com.cpmss.property.common.Area;
import com.cpmss.property.common.NonNegativeCount;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a unit.
 *
 * <p>Includes the owning building's ID and number for display.
 *
 * @param id                   the unit's UUID primary key
 * @param unitNo               the unit number
 * @param floorNo              floor number (may be {@code null})
 * @param noOfRooms            total rooms (may be {@code null})
 * @param noOfBathrooms        bathroom count (may be {@code null})
 * @param noOfBedrooms         bedroom count (may be {@code null})
 * @param noOfTotalRooms       total room count (may be {@code null})
 * @param noOfBalconies        balcony count (may be {@code null})
 * @param squareFoot           area (may be {@code null})
 * @param viewOrientation      view direction (may be {@code null})
 * @param gasMeterCode         gas meter ref (may be {@code null})
 * @param waterMeterCode       water meter ref (may be {@code null})
 * @param electricityMeterCode electricity meter ref (may be {@code null})
 * @param buildingId           the owning building's UUID
 * @param buildingNo           the owning building's number
 * @param createdAt            when the unit was created
 * @param updatedAt            when the unit was last modified
 */
public record UnitResponse(
        UUID id,
        String unitNo,
        Integer floorNo,
        NonNegativeCount noOfRooms,
        NonNegativeCount noOfBathrooms,
        NonNegativeCount noOfBedrooms,
        NonNegativeCount noOfTotalRooms,
        NonNegativeCount noOfBalconies,
        Area squareFoot,
        String viewOrientation,
        String gasMeterCode,
        String waterMeterCode,
        String electricityMeterCode,
        UUID buildingId,
        String buildingNo,
        Instant createdAt,
        Instant updatedAt
) {}
