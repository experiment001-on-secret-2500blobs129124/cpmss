package com.cpmss.security.vehicle.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a vehicle.
 *
 * <p>Includes owner IDs — exactly one will be non-null.
 *
 * @param id                the vehicle's UUID primary key
 * @param licenseNo         the license plate number
 * @param vehicleModel      model description (may be {@code null})
 * @param ownerPersonId     person owner UUID (may be {@code null})
 * @param ownerDepartmentId department owner UUID (may be {@code null})
 * @param ownerCompanyId    company owner UUID (may be {@code null})
 * @param createdAt         when the vehicle was created
 * @param updatedAt         when the vehicle was last modified
 */
public record VehicleResponse(
        UUID id,
        String licenseNo,
        String vehicleModel,
        UUID ownerPersonId,
        UUID ownerDepartmentId,
        UUID ownerCompanyId,
        Instant createdAt,
        Instant updatedAt
) {}
