package com.cpmss.performance.kpipolicy;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreConverter;
import com.cpmss.performance.common.KpiScoreRange;
import com.cpmss.performance.common.PercentageRate;
import com.cpmss.performance.common.PercentageRateConverter;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.common.PerformanceRatingConverter;
import com.cpmss.people.person.Person;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Catalog entity defining KPI scoring tiers and their payroll impact per department.
 *
 * <p>A time-bounded rule (like {@code Law_of_Shift_Attendance}).
 * Min/max KPI scores define the tier range (inclusive).
 * Tiers must not overlap within a department — enforced in
 * {@code KpiPolicyRules}.
 */
@Entity
@Table(name = "KPI_Policy")
@AttributeOverride(name = "id", column = @Column(name = "kpi_policy_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiPolicy extends BaseEntity {

    /** The department this policy applies to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** The date this policy became effective. */
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    /** Human-readable tier label (Excellent, Good, Average, Poor). */
    @Convert(converter = PerformanceRatingConverter.class)
    @Column(name = "tier_label", nullable = false, length = 50)
    @Setter(AccessLevel.NONE)
    private PerformanceRating tierLabel;

    /** Minimum KPI score for this tier (inclusive). */
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "min_kpi_score", nullable = false, precision = 5, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore minKpiScore;

    /** Maximum KPI score for this tier (inclusive). */
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "max_kpi_score", nullable = false, precision = 5, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore maxKpiScore;

    /** Bonus rate multiplier for this tier (default 0). */
    @Convert(converter = PercentageRateConverter.class)
    @Column(name = "bonus_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private PercentageRate bonusRate = PercentageRate.ZERO;

    /** Deduction rate multiplier for this tier (default 0). */
    @Convert(converter = PercentageRateConverter.class)
    @Column(name = "deduction_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private PercentageRate deductionRate = PercentageRate.ZERO;

    /** The manager who approved this policy. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", nullable = false)
    private Person approvedBy;

    /**
     * Returns the tier label for DTO compatibility.
     *
     * @return the database/API rating label, or {@code null} when unset
     */
    public String getTierLabel() {
        return tierLabel != null ? tierLabel.label() : null;
    }

    /**
     * Returns the typed performance rating for domain logic.
     *
     * @return the typed performance rating, or {@code null} when unset
     */
    public PerformanceRating getTierLabelValue() {
        return tierLabel;
    }

    /**
     * Returns the minimum KPI score for DTO compatibility.
     *
     * @return the minimum KPI score, or {@code null} when unset
     */
    public BigDecimal getMinKpiScore() {
        return minKpiScore != null ? minKpiScore.value() : null;
    }

    /**
     * Returns the typed minimum KPI score for domain logic.
     *
     * @return the typed minimum KPI score, or {@code null} when unset
     */
    public KpiScore getMinKpiScoreValue() {
        return minKpiScore;
    }

    /**
     * Returns the maximum KPI score for DTO compatibility.
     *
     * @return the maximum KPI score, or {@code null} when unset
     */
    public BigDecimal getMaxKpiScore() {
        return maxKpiScore != null ? maxKpiScore.value() : null;
    }

    /**
     * Returns the typed maximum KPI score for domain logic.
     *
     * @return the typed maximum KPI score, or {@code null} when unset
     */
    public KpiScore getMaxKpiScoreValue() {
        return maxKpiScore;
    }

    /**
     * Returns the inclusive score range for this tier.
     *
     * @return the typed KPI score range
     * @throws com.cpmss.platform.exception.BusinessException if either bound is
     *                                                        missing or invalid
     */
    public KpiScoreRange getScoreRange() {
        return new KpiScoreRange(minKpiScore, maxKpiScore);
    }

    /**
     * Returns the bonus rate for DTO compatibility.
     *
     * @return the bonus rate, or {@code null} when unset
     */
    public BigDecimal getBonusRate() {
        return bonusRate != null ? bonusRate.value() : null;
    }

    /**
     * Returns the typed bonus rate for domain logic.
     *
     * @return the typed bonus rate, or {@code null} when unset
     */
    public PercentageRate getBonusRateValue() {
        return bonusRate;
    }

    /**
     * Returns the deduction rate for DTO compatibility.
     *
     * @return the deduction rate, or {@code null} when unset
     */
    public BigDecimal getDeductionRate() {
        return deductionRate != null ? deductionRate.value() : null;
    }

    /**
     * Returns the typed deduction rate for domain logic.
     *
     * @return the typed deduction rate, or {@code null} when unset
     */
    public PercentageRate getDeductionRateValue() {
        return deductionRate;
    }

    /**
     * Assigns the required performance tier label.
     *
     * @param tierLabel the typed tier label
     * @throws IllegalArgumentException if the tier label is missing
     */
    public void setTierLabel(PerformanceRating tierLabel) {
        if (tierLabel == null) {
            throw new IllegalArgumentException("KPI tier label is required");
        }
        this.tierLabel = tierLabel;
    }

    /**
     * Assigns the required minimum KPI score.
     *
     * @param minKpiScore the typed minimum score
     * @throws IllegalArgumentException if the score is missing
     */
    public void setMinKpiScore(KpiScore minKpiScore) {
        if (minKpiScore == null) {
            throw new IllegalArgumentException("Minimum KPI score is required");
        }
        this.minKpiScore = minKpiScore;
    }

    /**
     * Assigns the required maximum KPI score.
     *
     * @param maxKpiScore the typed maximum score
     * @throws IllegalArgumentException if the score is missing
     */
    public void setMaxKpiScore(KpiScore maxKpiScore) {
        if (maxKpiScore == null) {
            throw new IllegalArgumentException("Maximum KPI score is required");
        }
        this.maxKpiScore = maxKpiScore;
    }

    /**
     * Assigns the required bonus rate.
     *
     * @param bonusRate the typed bonus rate
     * @throws IllegalArgumentException if the rate is missing
     */
    public void setBonusRate(PercentageRate bonusRate) {
        if (bonusRate == null) {
            throw new IllegalArgumentException("KPI bonus rate is required");
        }
        this.bonusRate = bonusRate;
    }

    /**
     * Assigns the required deduction rate.
     *
     * @param deductionRate the typed deduction rate
     * @throws IllegalArgumentException if the rate is missing
     */
    public void setDeductionRate(PercentageRate deductionRate) {
        if (deductionRate == null) {
            throw new IllegalArgumentException("KPI deduction rate is required");
        }
        this.deductionRate = deductionRate;
    }
}
