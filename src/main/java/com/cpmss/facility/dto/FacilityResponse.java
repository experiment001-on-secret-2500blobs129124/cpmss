package com.cpmss.facility.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a facility.
 *
 * <p>Includes denormalized building and optional company info.
 *
 * @param id                    the facility's UUID primary key
 * @param facilityName          display name
 * @param facilityCategory      classification (may be {@code null})
 * @param managementType        "Compound" or "Vendor"
 * @param buildingId            the owning building's UUID
 * @param buildingNo            the owning building's number
 * @param managedByCompanyId    managing company UUID (may be {@code null})
 * @param managedByCompanyName  managing company name (may be {@code null})
 * @param createdAt             when the facility was created
 * @param updatedAt             when the facility was last modified
 */
public record FacilityResponse(
        UUID id,
        String facilityName,
        String facilityCategory,
        String managementType,
        UUID buildingId,
        String buildingNo,
        UUID managedByCompanyId,
        String managedByCompanyName,
        Instant createdAt,
        Instant updatedAt
) {}
