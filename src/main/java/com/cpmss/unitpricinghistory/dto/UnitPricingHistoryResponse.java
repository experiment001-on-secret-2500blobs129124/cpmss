package com.cpmss.unitpricinghistory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a unit pricing history entry.
 */
public record UnitPricingHistoryResponse(
        UUID unitId,
        LocalDate effectiveDate,
        BigDecimal listingPrice
) {}
