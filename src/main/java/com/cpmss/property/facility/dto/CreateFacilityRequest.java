package com.cpmss.property.facility.dto;

import com.cpmss.property.common.FacilityManagementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a facility.
 *
 * <p>The building ID and management type are required. When
 * {@code managementType} is "Vendor", a company ID must also
 * be provided.
 *
 * @param facilityName       display name of the facility
 * @param facilityCategory   optional classification (e.g. Gym)
 * @param managementType     "Compound" or "Vendor"
 * @param buildingId         the owning building's UUID
 * @param managedByCompanyId optional managing company's UUID (required when Vendor)
 */
public record CreateFacilityRequest(
        @NotBlank @Size(max = 150) String facilityName,
        @Size(max = 50) String facilityCategory,
        @NotNull FacilityManagementType managementType,
        @NotNull UUID buildingId,
        UUID managedByCompanyId
) {}
