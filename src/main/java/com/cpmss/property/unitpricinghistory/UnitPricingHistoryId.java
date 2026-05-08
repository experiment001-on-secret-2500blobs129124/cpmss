package com.cpmss.property.unitpricinghistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link UnitPricingHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UnitPricingHistoryId implements Serializable {

    /** The unit whose pricing is tracked. */
    private UUID unit;

    /** The date this pricing became effective. */
    private LocalDate effectiveDate;
}
