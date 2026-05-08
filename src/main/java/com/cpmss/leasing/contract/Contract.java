package com.cpmss.leasing.contract;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.property.facility.Facility;
import com.cpmss.property.unit.Unit;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Owned entity representing a legally binding agreement between the compound and a tenant.
 *
 * <p>A contract covers exactly one target — either a {@link Unit}
 * (Residential) or a {@link Facility} (Commercial). Mutual exclusion
 * is enforced in {@link ContractRules} and by a CHECK constraint in V2.
 *
 * <p>Contracts are permanent records — closed by status change
 * (Terminated, Expired), never by deletion.
 */
@Entity
@Table(name = "Contract")
@AttributeOverride(name = "id", column = @Column(name = "contract_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    /** Human-readable document ID (e.g. "CON-2024-001"). */
    @Column(name = "contract_reference", nullable = false, unique = true, length = 50)
    private String contractReference;

    /** Contract start date. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Contract end date ({@code null} = open-ended). */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Contract type — Residential or Commercial. */
    @Column(name = "contract_type", nullable = false, length = 50)
    private String contractType;

    /** Contract lifecycle status (Draft, Active, Expired, Terminated, Renewed). */
    @Column(name = "contract_status", nullable = false, length = 50)
    private String contractStatus;

    /** Payment frequency (e.g. Monthly, Quarterly). */
    @Column(name = "payment_frequency", length = 50)
    private String paymentFrequency;

    /** Agreed final price. */
    @Column(name = "final_price", precision = 12, scale = 2)
    private BigDecimal finalPrice;

    /** Security deposit amount. */
    @Column(name = "security_deposit_amount", precision = 12, scale = 2)
    private BigDecimal securityDepositAmount;

    /** Free-text renewal terms. */
    @Column(name = "renewal_terms", columnDefinition = "TEXT")
    private String renewalTerms;

    /** Unit target — set for Residential contracts (mutually exclusive with facility). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    /** Facility target — set for Commercial contracts (mutually exclusive with unit). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;
}
