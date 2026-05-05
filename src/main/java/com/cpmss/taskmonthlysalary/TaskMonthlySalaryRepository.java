package com.cpmss.taskmonthlysalary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link TaskMonthlySalary} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * payroll lookups by department and period.
 */
public interface TaskMonthlySalaryRepository
        extends JpaRepository<TaskMonthlySalary, TaskMonthlySalaryId> {

    /**
     * Find all payroll records for a department in a given period.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month
     * @return monthly salary records
     */
    List<TaskMonthlySalary> findByDepartmentIdAndYearAndMonth(
            UUID departmentId, Integer year, Integer month);
}
