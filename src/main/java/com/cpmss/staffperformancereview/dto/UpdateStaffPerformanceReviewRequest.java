package com.cpmss.staffperformancereview.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request payload for updating an existing performance review.
 *
 * @param overallKpiScore      overall KPI score (optional)
 * @param overallRating        rating (optional)
 * @param notes                free-text review notes (optional)
 * @param resultedInPromotion  whether this review resulted in a promotion
 * @param resultedInRaise      whether this review resulted in a raise
 */
public record UpdateStaffPerformanceReviewRequest(
        BigDecimal overallKpiScore,
        @Size(max = 20) String overallRating,
        String notes,
        Boolean resultedInPromotion,
        Boolean resultedInRaise
) {}
