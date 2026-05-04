package com.cpmss.building.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for updating an existing building.
 *
 * <p>All fields are replaceable — the entire building record is
 * overwritten with the values supplied here.
 *
 * @param buildingNo       the building number within the compound
 * @param buildingName     optional display name
 * @param buildingType     optional classification
 * @param floorsCount      optional number of floors
 * @param constructionDate optional date of construction completion
 * @param compoundId       the owning compound's UUID
 */
public record UpdateBuildingRequest(
        @NotBlank @Size(max = 20) String buildingNo,
        @Size(max = 100) String buildingName,
        @Size(max = 50) String buildingType,
        Integer floorsCount,
        LocalDate constructionDate,
        @NotNull UUID compoundId
) {}
