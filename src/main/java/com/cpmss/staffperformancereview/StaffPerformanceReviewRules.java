package com.cpmss.staffperformancereview;

import com.cpmss.exception.BusinessException;
import com.cpmss.exception.ForbiddenException;

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
     * @throws ForbiddenException if staff and reviewer are the same person
     */
    public void validateReviewerIsNotSelf(UUID staffId, UUID reviewerId) {
        if (staffId.equals(reviewerId)) {
            throw new ForbiddenException("A staff member cannot review themselves");
        }
    }

    /**
     * Validates that promotion and raise flags are consistent with
     * the overall rating.
     *
     * <p>A "Poor" rating should not result in a promotion or raise.
     *
     * @param overallRating        the review rating (may be {@code null})
     * @param resultedInPromotion  whether the review resulted in a promotion
     * @param resultedInRaise      whether the review resulted in a raise
     * @throws BusinessException if flags are inconsistent with rating
     */
    public void validatePromotionConsistency(String overallRating,
                                              boolean resultedInPromotion,
                                              boolean resultedInRaise) {
        if ("Poor".equalsIgnoreCase(overallRating)
                && (resultedInPromotion || resultedInRaise)) {
            throw new BusinessException(
                    "A 'Poor' rating cannot result in a promotion or raise");
        }
    }
}
