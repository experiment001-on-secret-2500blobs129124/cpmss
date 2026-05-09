package com.cpmss.hr.staffposition;

import com.cpmss.hr.compensation.SalaryAmount;
import com.cpmss.hr.compensation.SalaryAmountConverter;
import com.cpmss.platform.common.BaseAuditEntity;
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

/**
 * SCD Type 2 entity tracking salary bands for a position over time.
 *
 * <p>Composite PK: ({@code position_id}, {@code salary_effective_date}).
 * The current salary band is the row with the latest effective date.
 */
@Entity
@Table(name = "Position_Salary_History")
@IdClass(PositionSalaryHistoryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionSalaryHistory extends BaseAuditEntity {

    /** The position this salary band applies to (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private StaffPosition position;

    /** The date this salary band became effective (part of composite PK). */
    @Id
    @Column(name = "salary_effective_date")
    private LocalDate salaryEffectiveDate;

    /** Maximum monthly salary for this position at this effective date. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "maximum_salary", nullable = false, precision = 12, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount maximumSalary;

    /** Base daily rate used for attendance-based pay calculation. */
    @Convert(converter = SalaryAmountConverter.class)
    @Column(name = "base_daily_rate", nullable = false, precision = 8, scale = 2)
    @Setter(lombok.AccessLevel.NONE)
    private SalaryAmount baseDailyRate;

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
     * Assigns the required maximum salary.
     *
     * @param maximumSalary the maximum salary
     */
    public void setMaximumSalary(BigDecimal maximumSalary) {
        this.maximumSalary = SalaryAmount.positive(maximumSalary);
    }

    /**
     * Assigns the required base daily rate.
     *
     * @param baseDailyRate the base daily rate
     */
    public void setBaseDailyRate(BigDecimal baseDailyRate) {
        this.baseDailyRate = SalaryAmount.positive(baseDailyRate);
    }
}
