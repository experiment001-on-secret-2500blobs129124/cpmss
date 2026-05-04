package com.cpmss.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a vehicle.
 *
 * <p>Exactly one of {@code ownerPersonId}, {@code ownerDepartmentId},
 * or {@code ownerCompanyId} must be set — enforced by
 * {@link com.cpmss.vehicle.VehicleRules}.
 *
 * @param licenseNo         the license plate number (system-wide unique)
 * @param vehicleModel      optional model description
 * @param ownerPersonId     optional person owner UUID
 * @param ownerDepartmentId optional department owner UUID
 * @param ownerCompanyId    optional company owner UUID
 */
public record CreateVehicleRequest(
        @NotBlank @Size(max = 20) String licenseNo,
        @Size(max = 100) String vehicleModel,
        UUID ownerPersonId,
        UUID ownerDepartmentId,
        UUID ownerCompanyId
) {}
