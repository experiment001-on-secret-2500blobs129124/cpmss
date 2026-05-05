package com.cpmss.departmentlocationhistory.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for adding a department location history entry.
 *
 * @param locationStartDate the date this location becomes active
 * @param buildingId        the building UUID where the department moves to
 */
public record CreateDeptLocationHistoryRequest(
        @NotNull LocalDate locationStartDate,
        @NotNull UUID buildingId
) {}
