package com.cpmss.performance.staffperformancereview;

import com.cpmss.performance.common.PerformanceErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.performance.common.PerformanceRating;

import java.util.UUID;

/**
 * Business rules for {@link StaffPerformanceReview} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see StaffPerformanceReviewService
 */
public class StaffPerformanceReviewRules {

    /**
     * Validates that the reviewer is not reviewing themselves.
     *
     * @param staffId    the staff member being reviewed
     * @param reviewerId the reviewing manager
     * @throws ApiException if staff and reviewer are the same person
     */
    public void validateReviewerIsNotSelf(UUID staffId, UUID reviewerId) {
        if (staffId.equals(reviewerId)) {
            throw new ApiException(PerformanceErrorCode.SELF_REVIEW_FORBIDDEN);
        }
    }

    /**
     * Validates that promotion and raise flags are consistent with
     * the overall rating.
     *
     * <p>A "Poor" rating should not result in a promotion or raise.
     *
     * @param overallRating        the review rating label (may be {@code null})
     * @param resultedInPromotion  whether the review resulted in a promotion
     * @param resultedInRaise      whether the review resulted in a raise
     * @return the typed performance rating, or {@code null} when absent
     * @throws ApiException if flags are inconsistent with rating
     */
    public PerformanceRating validatePromotionConsistency(String overallRating,
                                                          boolean resultedInPromotion,
                                                          boolean resultedInRaise) {
        PerformanceRating rating = PerformanceRating.fromNullableLabel(overallRating);
        if (rating == PerformanceRating.POOR
                && (resultedInPromotion || resultedInRaise)) {
            throw new ApiException(PerformanceErrorCode.POOR_RATING_INCONSISTENT);
        }
        return rating;
    }
}
