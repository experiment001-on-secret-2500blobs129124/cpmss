package com.cpmss.staffkpimonthlysummary;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link StaffKpiMonthlySummary} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffKpiMonthlySummaryRepository
        extends JpaRepository<StaffKpiMonthlySummary, StaffKpiMonthlySummaryId> {
}
