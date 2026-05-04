package com.cpmss.staffprofile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link StaffProfile} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. The primary key
 * is the person's UUID (shared PK with Person).
 */
public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID> {

    /**
     * Checks whether a staff profile exists for the given person.
     *
     * @param id the person's UUID
     * @return true if a staff profile exists
     */
    boolean existsById(UUID id);
}
