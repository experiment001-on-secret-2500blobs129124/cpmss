package com.cpmss.hr.staffsalaryhistory;

import com.cpmss.hr.compensation.SalaryAmount;
import com.cpmss.hr.compensation.SalaryAmountConverter;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.people.person.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.util.UUID;

/**
 * SCD Type 2 entity tracking individual staff compensation changes over time.
 *
 * <p>Composite PK: ({@code staff_id}, {@code effective_date}).
 * {@code end_date IS NULL} = currently active rate.
 * Each raise produces a new row; the initial rate comes from
 * {@code Hire_Agreement.offered_base_daily_rate}.
 */
@Entity
@Table(name = "Staff_Salary_History")
@IdClass(StaffSalaryHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffSalaryHistory extends BaseAuditEntity {

    /** The staff member (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Person staff;

    /** The date this rate became effective (part of composite PK). */
    @Id
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /** The date this rate was superseded ({@code null} = still active). */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Base daily rate for attendance-based pay. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "base_daily_rate", nullable = false, precision = 8, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount baseDailyRate;

    /** Maximum monthly salary cap. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "maximum_salary", nullable = false, precision = 12, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount maximumSalary;

    /** Manager who authorized this rate change ({@code null} for initial hire). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Person approvedBy;

    /** Performance review that triggered this change ({@code null} for initial hire). */
    @Column(name = "review_id")
    private UUID reviewId;

    /**
     * Returns the base daily rate amount for DTO compatibility.
     *
     * @return the base daily rate, or {@code null} when unset
     */
    public BigDecimal getBaseDailyRate() {
        return baseDailyRate != null ? baseDailyRate.amount() : null;
    }

    /**
     * Returns the typed base daily rate for domain logic.
     *
     * @return the typed base daily rate, or {@code null} when unset
     */
    public SalaryAmount getBaseDailyRateValue() {
        return baseDailyRate;
    }

    /**
     * Returns the maximum salary amount for DTO compatibility.
     *
     * @return the maximum salary, or {@code null} when unset
     */
    public BigDecimal getMaximumSalary() {
        return maximumSalary != null ? maximumSalary.amount() : null;
    }

    /**
     * Returns the typed maximum salary for domain logic.
     *
     * @return the typed maximum salary, or {@code null} when unset
     */
    public SalaryAmount getMaximumSalaryValue() {
        return maximumSalary;
    }

    /**
     * Assigns the required base daily rate.
     *
     * @param baseDailyRate the base daily rate
     */
    public void setBaseDailyRate(BigDecimal baseDailyRate) {
        this.baseDailyRate = SalaryAmount.positive(baseDailyRate);
    }

    /**
     * Assigns the required maximum salary.
     *
     * @param maximumSalary the maximum salary
     */
    public void setMaximumSalary(BigDecimal maximumSalary) {
        this.maximumSalary = SalaryAmount.positive(maximumSalary);
    }
}
