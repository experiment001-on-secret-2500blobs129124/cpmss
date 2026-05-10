package com.cpmss.workforce.taskmonthlysalary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
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

    /**
     * Finds payroll records for one staff member in a period.
     *
     * @param staffId the staff member UUID
     * @param year    the payroll year
     * @param month   the payroll month
     * @return payroll records for that staff member and period
     */
    List<TaskMonthlySalary> findByStaffIdAndYearAndMonth(
            UUID staffId, Integer year, Integer month);

    /**
     * Finds a closed payroll row for a specific staff, department, and month.
     *
     * @param staffId the staff member UUID
     * @param departmentId the department UUID
     * @param year the payroll year
     * @param month the payroll month
     * @return the payroll row, if closed
     */
    Optional<TaskMonthlySalary> findByStaffIdAndDepartmentIdAndYearAndMonth(
            UUID staffId, UUID departmentId, Integer year, Integer month);

    /**
     * Checks whether payroll was already closed for a staff/department/month.
     *
     * @param staffId the staff member UUID
     * @param departmentId the department UUID
     * @param year the payroll year
     * @param month the payroll month
     * @return true when a row already exists
     */
    boolean existsByStaffIdAndDepartmentIdAndYearAndMonth(
            UUID staffId, UUID departmentId, Integer year, Integer month);
}
