package com.cpmss.person;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link PersonRole} junction entities.
 *
 * <p>Provides CRUD for role assignments plus lookup and bulk-delete
 * by person ID for transactional role reassignment.
 */
public interface PersonRoleRepository extends JpaRepository<PersonRole, PersonRoleId> {

    /**
     * Finds all role assignments for a given person.
     *
     * @param personId the person's UUID
     * @return list of role assignments
     */
    List<PersonRole> findByPersonId(UUID personId);

    /**
     * Deletes all role assignments for a given person.
     *
     * <p>Used during update when the role set is being replaced.
     *
     * @param personId the person's UUID
     */
    void deleteByPersonId(UUID personId);
}
