package com.cpmss.staffperformancereview.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a performance review.
 */
public record StaffPerformanceReviewResponse(
        UUID id, UUID staffId, UUID reviewerId, UUID departmentId,
        LocalDate reviewDate, BigDecimal overallKpiScore, String overallRating,
        String notes, Boolean resultedInPromotion, Boolean resultedInRaise,
        Instant createdAt, Instant updatedAt
) {}
