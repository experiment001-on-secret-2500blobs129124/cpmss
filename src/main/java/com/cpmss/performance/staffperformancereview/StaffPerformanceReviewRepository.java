package com.cpmss.performance.staffperformancereview;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link StaffPerformanceReview} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffPerformanceReviewRepository
        extends JpaRepository<StaffPerformanceReview, UUID> {
}
