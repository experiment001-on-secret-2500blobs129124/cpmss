package com.cpmss.staffkpimonthlysummary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffKpiMonthlySummary} entities.
 */
public interface StaffKpiMonthlySummaryRepository
        extends JpaRepository<StaffKpiMonthlySummary, StaffKpiMonthlySummaryId> {

    /**
     * Find all KPI summaries for a department in a given period.
     */
    List<StaffKpiMonthlySummary> findByDepartmentIdAndYearAndMonth(
            UUID departmentId, Integer year, Integer month);
}
