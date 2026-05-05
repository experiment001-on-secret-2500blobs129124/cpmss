package com.cpmss.facilityhourshistory.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response payload for a facility hours history entry.
 *
 * @param facilityId     the facility UUID
 * @param effectiveDate  the date this schedule became active
 * @param openingTime    the opening time
 * @param closingTime    the closing time
 * @param operatingHours human-readable description
 */
public record FacilityHoursHistoryResponse(
        UUID facilityId,
        LocalDate effectiveDate,
        LocalTime openingTime,
        LocalTime closingTime,
        String operatingHours
) {}
