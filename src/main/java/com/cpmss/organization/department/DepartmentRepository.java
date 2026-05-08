package com.cpmss.organization.department;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Department} entities.
 */
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    /**
     * Checks whether a department with the given name exists.
     *
     * @param departmentName the name to check
     * @return true if a matching department exists
     */
    boolean existsByDepartmentName(String departmentName);
}
