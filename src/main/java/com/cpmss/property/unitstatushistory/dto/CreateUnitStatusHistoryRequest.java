package com.cpmss.property.unitstatushistory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request payload for adding a status history entry to a unit.
 *
 * @param effectiveDate the date this status becomes active
 * @param unitStatus    the status (Vacant, Occupied, Under Maintenance, Reserved)
 */
public record CreateUnitStatusHistoryRequest(
        @NotNull LocalDate effectiveDate,
        @NotBlank String unitStatus
) {}
