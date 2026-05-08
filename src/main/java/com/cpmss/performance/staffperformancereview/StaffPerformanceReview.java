package com.cpmss.performance.staffperformancereview;

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
 * Owned entity representing a formal periodic performance review.
 *
 * <p>{@code resulted_in_promotion = TRUE} → backend must create a new
 * {@code Staff_Position_History} row in the same transaction.
 * {@code resulted_in_raise = TRUE} → backend must create a new
 * {@code Staff_Salary_History} row. Both enforced in
 * {@code StaffPerformanceRules}.
 */
@Entity
@Table(name = "Staff_Performance_Review")
@AttributeOverride(name = "id", column = @Column(name = "review_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPerformanceReview extends BaseEntity {

    /** The staff member being reviewed. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Person staff;

    /** The reviewing manager (must be the direct supervisor). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Person reviewer;

    /** The department context for this review. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /** The date of the review. */
    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    /** Overall KPI score at time of review. */
    @Column(name = "overall_kpi_score", precision = 5, scale = 2)
    private BigDecimal overallKpiScore;

    /** Overall rating (Excellent, Good, Satisfactory, Poor). */
    @Column(name = "overall_rating", length = 20)
    private String overallRating;

    /** Free-text review notes. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Whether this review resulted in a promotion. */
    @Column(name = "resulted_in_promotion", nullable = false)
    @Builder.Default
    private Boolean resultedInPromotion = false;

    /** Whether this review resulted in a salary raise. */
    @Column(name = "resulted_in_raise", nullable = false)
    @Builder.Default
    private Boolean resultedInRaise = false;
}
