package com.cpmss.facilitymanager.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for assigning a manager to a facility.
 *
 * @param managerId           the manager's person UUID
 * @param managementStartDate the date this assignment begins
 */
public record CreateFacilityManagerRequest(
        @NotNull UUID managerId,
        @NotNull LocalDate managementStartDate
) {}
