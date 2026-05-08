package com.cpmss.property.facilityhourshistory.dto;

import com.cpmss.property.common.OperatingHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request payload for adding a facility hours history entry.
 *
 * @param effectiveDate  the date this schedule becomes active
 * @param operatingWindow the optional opening/closing time window
 * @param operatingHours human-readable description (e.g. "6AM-10PM")
 */
public record CreateFacilityHoursHistoryRequest(
        @NotNull LocalDate effectiveDate,
        @Valid OperatingHours operatingWindow,
        String operatingHours
) {}
