package com.cpmss.property.building.dto;

import com.cpmss.property.common.BuildingType;
import com.cpmss.property.common.NonNegativeCount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a building.
 *
 * <p>The compound ID is required — a building cannot exist
 * without a parent compound.
 *
 * @param buildingNo       the building number within the compound
 * @param buildingName     optional display name
 * @param buildingType     optional classification
 * @param floorsCount      optional number of floors
 * @param constructionDate optional date of construction completion
 * @param compoundId       the owning compound's UUID
 */
public record CreateBuildingRequest(
        @NotBlank @Size(max = 20) String buildingNo,
        @Size(max = 100) String buildingName,
        BuildingType buildingType,
        NonNegativeCount floorsCount,
        LocalDate constructionDate,
        @NotNull UUID compoundId
) {}
