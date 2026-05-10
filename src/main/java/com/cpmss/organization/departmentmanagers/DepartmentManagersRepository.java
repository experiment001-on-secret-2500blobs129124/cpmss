package com.cpmss.organization.departmentmanagers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link DepartmentManagers} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for department manager assignment lookups.
 */
public interface DepartmentManagersRepository
        extends JpaRepository<DepartmentManagers, DepartmentManagersId> {

    /**
     * Finds all manager assignments for a department, ordered by start date descending.
     *
     * @param departmentId the department's UUID
     * @return manager assignments, most recent first
     */
    List<DepartmentManagers> findByDepartmentIdOrderByManagementStartDateDesc(UUID departmentId);

    /**
     * Finds the current manager assignment for a department.
     *
     * @param departmentId the department UUID
     * @return the active manager assignment, if present
     */
    Optional<DepartmentManagers> findFirstByDepartmentIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
            UUID departmentId);
}
