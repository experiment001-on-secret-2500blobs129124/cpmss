package com.cpmss.performance.staffperformancereview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a performance review.
 *
 * @param staffId              the staff member UUID
 * @param reviewerId           the reviewing manager UUID
 * @param departmentId         the department UUID
 * @param reviewDate           the date of the review
 * @param overallKpiScore      overall KPI score (optional)
 * @param overallRating        rating (optional)
 * @param notes                free-text review notes (optional)
 * @param resultedInPromotion  whether this review resulted in a promotion
 * @param resultedInRaise      whether this review resulted in a raise
 */
public record CreateStaffPerformanceReviewRequest(
        @NotNull UUID staffId,
        @NotNull UUID reviewerId,
        @NotNull UUID departmentId,
        @NotNull LocalDate reviewDate,
        BigDecimal overallKpiScore,
        @Size(max = 20) String overallRating,
        String notes,
        Boolean resultedInPromotion,
        Boolean resultedInRaise
) {}
