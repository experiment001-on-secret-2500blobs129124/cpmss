package com.cpmss.property.unitpricinghistory.dto;

import com.cpmss.finance.money.Money;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a unit pricing history entry.
 */
public record UnitPricingHistoryResponse(
        UUID unitId,
        LocalDate effectiveDate,
        Money listingPrice
) {}
