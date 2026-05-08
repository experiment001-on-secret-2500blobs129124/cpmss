package com.cpmss.organization.departmentlocationhistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link DepartmentLocationHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 department location lookups.
 */
public interface DepartmentLocationHistoryRepository
        extends JpaRepository<DepartmentLocationHistory, DepartmentLocationHistoryId> {

    /**
     * Finds all location history entries for a department, ordered by start date descending.
     *
     * @param departmentId the department's UUID
     * @return location history entries, most recent first
     */
    List<DepartmentLocationHistory> findByDepartmentIdOrderByLocationStartDateInBuildingDesc(
            UUID departmentId);
}
