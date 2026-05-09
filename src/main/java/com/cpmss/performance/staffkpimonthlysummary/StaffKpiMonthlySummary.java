package com.cpmss.performance.staffkpimonthlysummary;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreConverter;
import com.cpmss.performance.common.PercentageRate;
import com.cpmss.performance.common.PercentageRateConverter;
import com.cpmss.performance.common.PerformanceRating;
import com.cpmss.performance.common.PerformanceRatingConverter;
import com.cpmss.performance.kpipolicy.KpiPolicy;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Month-end rollup of daily KPI scores per staff per department.
 *
 * <p>Composite PK: ({@code staff_id}, {@code department_id},
 * {@code year}, {@code month}). Snapshot values — do not recalculate
 * after month is closed.
 */
@Entity
@Table(name = "Staff_KPI_Monthly_Summary")
@IdClass(StaffKpiMonthlySummaryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffKpiMonthlySummary extends BaseAuditEntity {

    /** The staff member (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Person staff;

    /** The department (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** The summary year (part of composite PK). */
    @Id
    @Column(name = "year")
    private Integer year;

    /** The summary month (part of composite PK). */
    @Id
    @Column(name = "month")
    private Integer month;

    /** Average KPI score for the month. */
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "avg_kpi_score", nullable = false, precision = 5, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore avgKpiScore;

    /** Total KPI score for the month. */
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "total_kpi_score", nullable = false, precision = 8, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore totalKpiScore;

    /** Number of days scored. */
    @Column(name = "days_scored", nullable = false)
    private Integer daysScored;

    /** The applicable tier label at close time. */
    @Convert(converter = PerformanceRatingConverter.class)
    @Column(name = "applicable_tier", length = 50)
    @Setter(AccessLevel.NONE)
    private PerformanceRating applicableTier;

    /** Payroll bonus rate from policy at close time. */
    @Convert(converter = PercentageRateConverter.class)
    @Column(name = "payroll_bonus_rate", precision = 5, scale = 4)
    @Setter(AccessLevel.NONE)
    private PercentageRate payrollBonusRate;

    /** Payroll deduction rate from policy at close time. */
    @Convert(converter = PercentageRateConverter.class)
    @Column(name = "payroll_deduct_rate", precision = 5, scale = 4)
    @Setter(AccessLevel.NONE)
    private PercentageRate payrollDeductRate;

    /** The KPI policy used at close time. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_policy_id")
    private KpiPolicy kpiPolicy;

    /** The manager who closed this month's summary. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id", nullable = false)
    private Person closedBy;

    /**
     * Returns the average KPI score for DTO compatibility.
     *
     * @return the average score, or {@code null} when unset
     */
    public BigDecimal getAvgKpiScore() {
        return avgKpiScore != null ? avgKpiScore.value() : null;
    }

    /**
     * Returns the typed average KPI score for domain logic.
     *
     * @return the typed average score, or {@code null} when unset
     */
    public KpiScore getAvgKpiScoreValue() {
        return avgKpiScore;
    }

    /**
     * Returns the total KPI score for DTO compatibility.
     *
     * @return the total score, or {@code null} when unset
     */
    public BigDecimal getTotalKpiScore() {
        return totalKpiScore != null ? totalKpiScore.value() : null;
    }

    /**
     * Returns the typed total KPI score for domain logic.
     *
     * @return the typed total score, or {@code null} when unset
     */
    public KpiScore getTotalKpiScoreValue() {
        return totalKpiScore;
    }

    /**
     * Returns the applicable tier label for DTO compatibility.
     *
     * @return the database/API rating label, or {@code null} when unset
     */
    public String getApplicableTier() {
        return applicableTier != null ? applicableTier.label() : null;
    }

    /**
     * Returns the typed applicable tier for domain logic.
     *
     * @return the typed applicable tier, or {@code null} when unset
     */
    public PerformanceRating getApplicableTierValue() {
        return applicableTier;
    }

    /**
     * Returns the payroll bonus rate for DTO compatibility.
     *
     * @return the payroll bonus rate, or {@code null} when unset
     */
    public BigDecimal getPayrollBonusRate() {
        return payrollBonusRate != null ? payrollBonusRate.value() : null;
    }

    /**
     * Returns the typed payroll bonus rate for domain logic.
     *
     * @return the typed payroll bonus rate, or {@code null} when unset
     */
    public PercentageRate getPayrollBonusRateValue() {
        return payrollBonusRate;
    }

    /**
     * Returns the payroll deduction rate for DTO compatibility.
     *
     * @return the payroll deduction rate, or {@code null} when unset
     */
    public BigDecimal getPayrollDeductRate() {
        return payrollDeductRate != null ? payrollDeductRate.value() : null;
    }

    /**
     * Returns the typed payroll deduction rate for domain logic.
     *
     * @return the typed payroll deduction rate, or {@code null} when unset
     */
    public PercentageRate getPayrollDeductRateValue() {
        return payrollDeductRate;
    }

    /**
     * Assigns the required average KPI score.
     *
     * @param avgKpiScore the typed average score
     * @throws IllegalArgumentException if the score is missing
     */
    public void setAvgKpiScore(KpiScore avgKpiScore) {
        if (avgKpiScore == null) {
            throw new IllegalArgumentException("Average KPI score is required");
        }
        this.avgKpiScore = avgKpiScore;
    }

    /**
     * Assigns the required total KPI score.
     *
     * @param totalKpiScore the typed total score
     * @throws IllegalArgumentException if the score is missing
     */
    public void setTotalKpiScore(KpiScore totalKpiScore) {
        if (totalKpiScore == null) {
            throw new IllegalArgumentException("Total KPI score is required");
        }
        this.totalKpiScore = totalKpiScore;
    }

    /**
     * Assigns the optional applicable performance tier.
     *
     * @param applicableTier the typed applicable tier
     */
    public void setApplicableTier(PerformanceRating applicableTier) {
        this.applicableTier = applicableTier;
    }

    /**
     * Assigns the optional payroll bonus rate snapshot.
     *
     * @param payrollBonusRate the typed payroll bonus rate
     */
    public void setPayrollBonusRate(PercentageRate payrollBonusRate) {
        this.payrollBonusRate = payrollBonusRate;
    }

    /**
     * Assigns the optional payroll deduction rate snapshot.
     *
     * @param payrollDeductRate the typed payroll deduction rate
     */
    public void setPayrollDeductRate(PercentageRate payrollDeductRate) {
        this.payrollDeductRate = payrollDeductRate;
    }
}
