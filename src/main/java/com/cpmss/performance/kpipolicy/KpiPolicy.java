package com.cpmss.performance.kpipolicy;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.people.person.Person;
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

    /** Human-readable tier label (e.g. "Excellent", "Good", "Poor"). */
    @Column(name = "tier_label", nullable = false, length = 50)
    private String tierLabel;

    /** Minimum KPI score for this tier (inclusive). */
    @Column(name = "min_kpi_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal minKpiScore;

    /** Maximum KPI score for this tier (inclusive). */
    @Column(name = "max_kpi_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxKpiScore;

    /** Bonus rate multiplier for this tier (default 0). */
    @Column(name = "bonus_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal bonusRate = BigDecimal.ZERO;

    /** Deduction rate multiplier for this tier (default 0). */
    @Column(name = "deduction_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal deductionRate = BigDecimal.ZERO;

    /** The manager who approved this policy. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", nullable = false)
    private Person approvedBy;
}
