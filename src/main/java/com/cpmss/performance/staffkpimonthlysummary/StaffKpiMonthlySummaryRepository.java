package com.cpmss.performance.staffkpimonthlysummary;

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

    /**
     * Finds all KPI summaries for one staff member in a given period.
     *
     * @param staffId the staff member UUID
     * @param year    the summary year
     * @param month   the summary month
     * @return monthly KPI summaries for that staff member and period
     */
    List<StaffKpiMonthlySummary> findByStaffIdAndYearAndMonth(
            UUID staffId, Integer year, Integer month);

    /**
     * Checks whether KPI close already exists for staff/department/month.
     *
     * @param staffId the staff member UUID
     * @param departmentId the department UUID
     * @param year the close year
     * @param month the close month
     * @return true when the monthly summary already exists
     */
    boolean existsByStaffIdAndDepartmentIdAndYearAndMonth(
            UUID staffId, UUID departmentId, Integer year, Integer month);
}
