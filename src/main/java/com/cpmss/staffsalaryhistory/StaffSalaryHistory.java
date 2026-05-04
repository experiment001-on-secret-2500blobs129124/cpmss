package com.cpmss.staffsalaryhistory;

import com.cpmss.common.BaseAuditEntity;
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
    @Column(name = "base_daily_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal baseDailyRate;

    /** Maximum monthly salary cap. */
    @Column(name = "maximum_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal maximumSalary;

    /** Manager who authorized this rate change ({@code null} for initial hire). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Person approvedBy;

    /** Performance review that triggered this change ({@code null} for initial hire). */
    @Column(name = "review_id")
    private UUID reviewId;
}
