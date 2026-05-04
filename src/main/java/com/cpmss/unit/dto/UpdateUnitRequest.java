package com.cpmss.unit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request payload for updating an existing unit.
 *
 * <p>All fields are replaceable — the entire unit record is
 * overwritten with the values supplied here.
 *
 * @param unitNo              the unit number within the building
 * @param floorNo             optional floor number
 * @param noOfRooms           optional total rooms
 * @param noOfBathrooms       optional bathroom count
 * @param noOfBedrooms        optional bedroom count
 * @param noOfTotalRooms      optional total room count
 * @param noOfBalconies       optional balcony count
 * @param squareFoot          optional area in square feet
 * @param viewOrientation     optional view direction
 * @param gasMeterCode        optional gas meter reference
 * @param waterMeterCode      optional water meter reference
 * @param electricityMeterCode optional electricity meter reference
 * @param buildingId          the owning building's UUID
 */
public record UpdateUnitRequest(
        @NotBlank @Size(max = 20) String unitNo,
        Integer floorNo,
        Integer noOfRooms,
        Integer noOfBathrooms,
        Integer noOfBedrooms,
        Integer noOfTotalRooms,
        Integer noOfBalconies,
        BigDecimal squareFoot,
        @Size(max = 50) String viewOrientation,
        @Size(max = 50) String gasMeterCode,
        @Size(max = 50) String waterMeterCode,
        @Size(max = 50) String electricityMeterCode,
        @NotNull UUID buildingId
) {}
