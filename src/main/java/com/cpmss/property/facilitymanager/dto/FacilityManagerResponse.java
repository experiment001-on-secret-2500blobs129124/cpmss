package com.cpmss.property.facilitymanager.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a facility manager assignment.
 *
 * @param facilityId           the facility UUID
 * @param managerId            the manager's person UUID
 * @param managementStartDate  the assignment start date
 * @param managementEndDate    the assignment end date ({@code null} = still active)
 */
public record FacilityManagerResponse(
        UUID facilityId,
        UUID managerId,
        LocalDate managementStartDate,
        LocalDate managementEndDate
) {}
