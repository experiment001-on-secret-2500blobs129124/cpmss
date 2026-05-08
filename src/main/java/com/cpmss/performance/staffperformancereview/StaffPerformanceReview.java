package com.cpmss.performance.staffperformancereview;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.organization.department.Department;
import com.cpmss.performance.common.KpiScore;
import com.cpmss.performance.common.KpiScoreConverter;
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
    @Convert(converter = KpiScoreConverter.class)
    @Column(name = "overall_kpi_score", precision = 5, scale = 2)
    @Setter(AccessLevel.NONE)
    private KpiScore overallKpiScore;

    /** Overall rating (Excellent, Good, Average, Poor). */
    @Convert(converter = PerformanceRatingConverter.class)
    @Column(name = "overall_rating", length = 20)
    @Setter(AccessLevel.NONE)
    private PerformanceRating overallRating;

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

    /**
     * Returns the overall KPI score for DTO compatibility.
     *
     * @return the overall KPI score, or {@code null} when absent
     */
    public BigDecimal getOverallKpiScore() {
        return overallKpiScore != null ? overallKpiScore.value() : null;
    }

    /**
     * Returns the typed overall KPI score for domain logic.
     *
     * @return the typed overall KPI score, or {@code null} when absent
     */
    public KpiScore getOverallKpiScoreValue() {
        return overallKpiScore;
    }

    /**
     * Returns the overall rating for DTO compatibility.
     *
     * @return the database/API rating label, or {@code null} when absent
     */
    public String getOverallRating() {
        return overallRating != null ? overallRating.label() : null;
    }

    /**
     * Returns the typed overall rating for domain logic.
     *
     * @return the typed overall rating, or {@code null} when absent
     */
    public PerformanceRating getOverallRatingValue() {
        return overallRating;
    }

    /**
     * Assigns the optional overall KPI score.
     *
     * @param overallKpiScore the typed overall score
     */
    public void setOverallKpiScore(KpiScore overallKpiScore) {
        this.overallKpiScore = overallKpiScore;
    }

    /**
     * Assigns the optional overall performance rating.
     *
     * @param overallRating the typed overall rating
     */
    public void setOverallRating(PerformanceRating overallRating) {
        this.overallRating = overallRating;
    }
}
