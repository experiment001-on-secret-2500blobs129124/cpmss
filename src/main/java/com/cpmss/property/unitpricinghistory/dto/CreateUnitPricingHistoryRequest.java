package com.cpmss.property.unitpricinghistory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for adding a pricing history entry to a unit.
 *
 * @param effectiveDate the date this price becomes active
 * @param listingPrice  the listing price
 */
public record CreateUnitPricingHistoryRequest(
        @NotNull LocalDate effectiveDate,
        @NotNull @Positive BigDecimal listingPrice
) {}
