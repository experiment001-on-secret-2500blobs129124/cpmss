package com.cpmss.taskmonthlysalary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link TaskMonthlySalary} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for monthly payroll lookups by department and period.
 */
public interface TaskMonthlySalaryRepository
        extends JpaRepository<TaskMonthlySalary, TaskMonthlySalaryId> {

    /**
     * Finds all payroll records for a department in a given period.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month
     * @return monthly salary records for that department and period
     */
    List<TaskMonthlySalary> findByDepartmentIdAndYearAndMonth(
            UUID departmentId, Integer year, Integer month);
}
