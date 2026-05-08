package com.cpmss.property.unit.dto;

import com.cpmss.property.common.Area;
import com.cpmss.property.common.NonNegativeCount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a unit.
 *
 * <p>The building ID is required — a unit cannot exist
 * without a parent building.
 *
 * @param unitNo              the unit number within the building
 * @param floorNo             optional floor number
 * @param noOfRooms           optional total rooms
 * @param noOfBathrooms       optional bathroom count
 * @param noOfBedrooms        optional bedroom count
 * @param noOfTotalRooms      optional total room count (all types)
 * @param noOfBalconies       optional balcony count
 * @param squareFoot          optional area in square feet
 * @param viewOrientation     optional view direction
 * @param gasMeterCode        optional gas meter reference
 * @param waterMeterCode      optional water meter reference
 * @param electricityMeterCode optional electricity meter reference
 * @param buildingId          the owning building's UUID
 */
public record CreateUnitRequest(
        @NotBlank @Size(max = 20) String unitNo,
        Integer floorNo,
        NonNegativeCount noOfRooms,
        NonNegativeCount noOfBathrooms,
        NonNegativeCount noOfBedrooms,
        NonNegativeCount noOfTotalRooms,
        NonNegativeCount noOfBalconies,
        Area squareFoot,
        @Size(max = 50) String viewOrientation,
        @Size(max = 50) String gasMeterCode,
        @Size(max = 50) String waterMeterCode,
        @Size(max = 50) String electricityMeterCode,
        @NotNull UUID buildingId
) {}
