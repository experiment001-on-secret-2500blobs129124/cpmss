package com.cpmss.staffposition;

import com.cpmss.common.BaseAuditEntity;
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
    @Column(name = "maximum_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal maximumSalary;

    /** Base daily rate used for attendance-based pay calculation. */
    @Column(name = "base_daily_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal baseDailyRate;
}
