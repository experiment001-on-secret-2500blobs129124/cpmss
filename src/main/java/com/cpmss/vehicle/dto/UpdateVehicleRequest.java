package com.cpmss.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing vehicle.
 *
 * <p>All fields are replaceable. Exactly one owner must be set.
 *
 * @param licenseNo         the license plate number (system-wide unique)
 * @param vehicleModel      optional model description
 * @param ownerPersonId     optional person owner UUID
 * @param ownerDepartmentId optional department owner UUID
 * @param ownerCompanyId    optional company owner UUID
 */
public record UpdateVehicleRequest(
        @NotBlank @Size(max = 20) String licenseNo,
        @Size(max = 100) String vehicleModel,
        UUID ownerPersonId,
        UUID ownerDepartmentId,
        UUID ownerCompanyId
) {}
