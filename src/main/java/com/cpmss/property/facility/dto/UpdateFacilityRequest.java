package com.cpmss.property.facility.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing facility.
 *
 * <p>All fields are replaceable. When {@code managementType} is
 * "Vendor", the company ID must also be provided.
 *
 * @param facilityName       display name of the facility
 * @param facilityCategory   optional classification
 * @param managementType     "Compound" or "Vendor"
 * @param buildingId         the owning building's UUID
 * @param managedByCompanyId optional managing company's UUID
 */
public record UpdateFacilityRequest(
        @NotBlank @Size(max = 150) String facilityName,
        @Size(max = 50) String facilityCategory,
        @NotBlank @Size(max = 20) String managementType,
        @NotNull UUID buildingId,
        UUID managedByCompanyId
) {}
