package com.cpmss.security.vehicle.dto;

import java.util.UUID;

/**
 * Response payload for a vehicle-permit link workflow.
 *
 * @param vehicleId the linked vehicle UUID
 * @param permitId the linked access permit UUID
 * @param licenseNo the vehicle license number
 * @param permitNo the access permit number
 */
public record VehiclePermitLinkResponse(
        UUID vehicleId,
        UUID permitId,
        String licenseNo,
        String permitNo
) {}
