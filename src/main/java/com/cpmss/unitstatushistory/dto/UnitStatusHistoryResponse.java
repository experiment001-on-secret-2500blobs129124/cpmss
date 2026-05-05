package com.cpmss.unitstatushistory.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a unit status history entry.
 */
public record UnitStatusHistoryResponse(
        UUID unitId,
        LocalDate effectiveDate,
        String unitStatus
) {}
