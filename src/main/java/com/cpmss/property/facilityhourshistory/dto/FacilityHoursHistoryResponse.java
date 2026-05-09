package com.cpmss.property.facilityhourshistory.dto;

import com.cpmss.property.common.OperatingHours;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a facility hours history entry.
 *
 * @param facilityId     the facility UUID
 * @param effectiveDate  the date this schedule became active
 * @param operatingWindow optional opening/closing time window
 * @param operatingHours human-readable description
 */
public record FacilityHoursHistoryResponse(
        UUID facilityId,
        LocalDate effectiveDate,
        OperatingHours operatingWindow,
        String operatingHours
) {}
