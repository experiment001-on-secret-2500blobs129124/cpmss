package com.cpmss.property.facilityhourshistory.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request payload for adding a facility hours history entry.
 *
 * @param effectiveDate  the date this schedule becomes active
 * @param openingTime    the opening time
 * @param closingTime    the closing time
 * @param operatingHours human-readable description (e.g. "6AM-10PM")
 */
public record CreateFacilityHoursHistoryRequest(
        @NotNull LocalDate effectiveDate,
        LocalTime openingTime,
        LocalTime closingTime,
        String operatingHours
) {}
