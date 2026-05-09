package com.cpmss.property.unitpricinghistory.dto;

import com.cpmss.finance.money.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request payload for adding a pricing history entry to a unit.
 *
 * @param effectiveDate the date this price becomes active
 * @param listingPrice  the listing price money
 */
public record CreateUnitPricingHistoryRequest(
        @NotNull LocalDate effectiveDate,
        @NotNull @Valid Money listingPrice
) {}
