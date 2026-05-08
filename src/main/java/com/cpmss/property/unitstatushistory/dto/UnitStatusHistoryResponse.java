package com.cpmss.property.unitstatushistory.dto;

import com.cpmss.property.common.UnitStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a unit status history entry.
 */
public record UnitStatusHistoryResponse(
        UUID unitId,
        LocalDate effectiveDate,
        UnitStatus unitStatus
) {}
