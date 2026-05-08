package com.cpmss.property.unitpricinghistory;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.property.unit.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * SCD Type 2 entity tracking listing price of a unit over time.
 *
 * <p>Composite PK: ({@code unit_id}, {@code effective_date}).
 * {@code ORDER BY effective_date DESC LIMIT 1} = current listing price.
 */
@Entity
@Table(name = "Unit_Pricing_History")
@IdClass(UnitPricingHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitPricingHistory extends BaseAuditEntity {

    /** The unit whose pricing is tracked (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    /** The date this pricing became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /** Listing price for the unit at this effective date. */
    @Column(name = "listing_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal listingPrice;
}
