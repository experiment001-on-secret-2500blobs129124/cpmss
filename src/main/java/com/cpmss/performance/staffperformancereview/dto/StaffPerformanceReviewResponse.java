package com.cpmss.performance.staffperformancereview.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a performance review.
 *
 * @param id the review UUID
 * @param staffId the reviewed staff member UUID
 * @param reviewerId the reviewing manager UUID
 * @param departmentId the review department UUID
 * @param reviewDate the review date
 * @param overallKpiScore the optional overall KPI score
 * @param overallRating the optional overall rating label
 * @param notes optional free-text review notes
 * @param resultedInPromotion whether the review resulted in promotion
 * @param resultedInRaise whether the review resulted in a salary raise
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record StaffPerformanceReviewResponse(
        UUID id, UUID staffId, UUID reviewerId, UUID departmentId,
        LocalDate reviewDate, BigDecimal overallKpiScore, String overallRating,
        String notes, Boolean resultedInPromotion, Boolean resultedInRaise,
        Instant createdAt, Instant updatedAt
) {}
