package com.cpmss.performance.staffkpirecord;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreConverter;
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
import java.time.LocalDate;

/**
 * Junction entity recording daily KPI scores for staff in a department.
 *
 * <p>Composite PK: ({@code staff_id}, {@code department_id}, {@code record_date}).
 * Daily scores are rolled up into {@code Staff_KPI_Monthly_Summary} at month-end.
 */
@Entity
@Table(name = "Staff_KPI_Record")
@IdClass(StaffKpiRecordId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffKpiRecord extends BaseAuditEntity {

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

    /** The date the KPI was recorded (part of composite PK). */
    @Id
    @Column(name = "record_date")
    private LocalDate recordDate;

    /** The KPI score for this date. */
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "kpi_score", nullable = false, precision = 5, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore kpiScore;

    /** The KPI policy tier applicable for this record. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_policy_id", nullable = false)
    private KpiPolicy kpiPolicy;

    /** The manager who recorded this score. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private Person recordedBy;

    /** Optional evaluator notes. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Returns the KPI score for DTO compatibility.
     *
     * @return the KPI score, or {@code null} when unset
     */
    public BigDecimal getKpiScore() {
        return kpiScore != null ? kpiScore.value() : null;
    }

    /**
     * Returns the typed KPI score for domain logic.
     *
     * @return the typed KPI score, or {@code null} when unset
     */
    public KpiScore getKpiScoreValue() {
        return kpiScore;
    }

    /**
     * Assigns the required KPI score.
     *
     * @param kpiScore the typed KPI score
     * @throws IllegalArgumentException if the score is missing
     */
    public void setKpiScore(KpiScore kpiScore) {
        if (kpiScore == null) {
            throw new IllegalArgumentException("KPI score is required");
        }
        this.kpiScore = kpiScore;
    }
}
