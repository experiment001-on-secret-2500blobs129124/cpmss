package com.cpmss.staffkpimonthlysummary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffKpiMonthlySummary} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for monthly KPI summary lookups by department and period.
 */
public interface StaffKpiMonthlySummaryRepository
        extends JpaRepository<StaffKpiMonthlySummary, StaffKpiMonthlySummaryId> {

    /**
     * Finds all KPI summaries for a department in a given period.
     *
     * @param departmentId the department UUID
     * @param year         the summary year
     * @param month        the summary month
     * @return monthly KPI summaries for that department and period
     */
    List<StaffKpiMonthlySummary> findByDepartmentIdAndYearAndMonth(
            UUID departmentId, Integer year, Integer month);
}
