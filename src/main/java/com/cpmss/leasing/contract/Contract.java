package com.cpmss.leasing.contract;

import com.cpmss.finance.money.Money;
import com.cpmss.leasing.common.ContractPeriod;
import com.cpmss.leasing.common.ContractStatus;
import com.cpmss.leasing.common.ContractStatusConverter;
import com.cpmss.leasing.common.ContractType;
import com.cpmss.leasing.common.ContractTypeConverter;
import com.cpmss.platform.common.BaseEntity;
import com.cpmss.property.facility.Facility;
import com.cpmss.property.unit.Unit;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@AttributeOverride(name = "id", column = @Column(name = "contract_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    /** Human-readable document ID (e.g. "CON-2024-001"). */
    @Column(name = "contract_reference", nullable = false, unique = true, length = 50)
    private String contractReference;

    /** Contract start/end date period mapped to the existing date columns. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate",
                    column = @Column(name = "start_date", nullable = false)),
            @AttributeOverride(name = "endDate",
                    column = @Column(name = "end_date"))
    })
    @Setter(AccessLevel.NONE)
    private ContractPeriod period;

    /** Contract type — Residential or Commercial. */
    @Convert(converter = ContractTypeConverter.class)
    @Column(name = "contract_type", nullable = false, length = 50)
    @Setter(AccessLevel.NONE)
    private ContractType contractType;

    /** Contract lifecycle status (Draft, Active, Expired, Terminated, Renewed). */
    @Convert(converter = ContractStatusConverter.class)
    @Column(name = "contract_status", nullable = false, length = 50)
    @Setter(AccessLevel.NONE)
    private ContractStatus contractStatus;

    /** Payment frequency (e.g. Monthly, Quarterly). */
    @Column(name = "payment_frequency", length = 50)
    private String paymentFrequency;

    /** Optional agreed final price with explicit currency. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "final_price", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "final_price_currency", length = 10))
    })
    private Money finalPrice;

    /** Optional security deposit with explicit currency. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "security_deposit_amount", precision = 12, scale = 2)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "security_deposit_currency", length = 10))
    })
    private Money securityDeposit;

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

    /**
     * Returns the contract start date for existing workflow code.
     *
     * @return the contract start date, or {@code null} when the period is unset
     */
    public LocalDate getStartDate() {
        return period != null ? period.getStartDate() : null;
    }

    /**
     * Returns the contract end date for existing workflow code.
     *
     * @return the optional contract end date, or {@code null} when absent
     */
    public LocalDate getEndDate() {
        return period != null ? period.getEndDate() : null;
    }

    /**
     * Assigns the contract period.
     *
     * @param period the validated contract period
     * @throws IllegalArgumentException if the period is missing
     */
    public void setPeriod(ContractPeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("Contract period is required");
        }
        this.period = period;
    }

    /**
     * Assigns the typed contract type.
     *
     * @param contractType the contract type
     * @throws IllegalArgumentException if the type is missing
     */
    public void setContractType(ContractType contractType) {
        if (contractType == null) {
            throw new IllegalArgumentException("Contract type is required");
        }
        this.contractType = contractType;
    }

    /**
     * Assigns the typed contract status.
     *
     * @param contractStatus the contract status
     * @throws IllegalArgumentException if the status is missing
     */
    public void setContractStatus(ContractStatus contractStatus) {
        if (contractStatus == null) {
            throw new IllegalArgumentException("Contract status is required");
        }
        this.contractStatus = contractStatus;
    }
}
