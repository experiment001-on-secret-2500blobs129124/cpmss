package com.cpmss.staffkpimonthlysummary;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.department.Department;
import com.cpmss.kpipolicy.KpiPolicy;
import com.cpmss.person.Person;
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
    @Column(name = "avg_kpi_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal avgKpiScore;

    /** Total KPI score for the month. */
    @Column(name = "total_kpi_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalKpiScore;

    /** Number of days scored. */
    @Column(name = "days_scored", nullable = false)
    private Integer daysScored;

    /** The applicable tier label at close time. */
    @Column(name = "applicable_tier", length = 50)
    private String applicableTier;

    /** Payroll bonus rate from policy at close time. */
    @Column(name = "payroll_bonus_rate", precision = 5, scale = 4)
    private BigDecimal payrollBonusRate;

    /** Payroll deduction rate from policy at close time. */
    @Column(name = "payroll_deduct_rate", precision = 5, scale = 4)
    private BigDecimal payrollDeductRate;

    /** The KPI policy used at close time. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_policy_id")
    private KpiPolicy kpiPolicy;

    /** The manager who closed this month's summary. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id", nullable = false)
    private Person closedBy;
}
