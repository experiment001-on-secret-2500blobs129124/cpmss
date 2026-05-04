package com.cpmss.staffkpirecord;

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
    @Column(name = "kpi_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal kpiScore;

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
}
