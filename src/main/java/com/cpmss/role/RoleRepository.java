package com.cpmss.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Role} entities.
 */
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Checks whether a role with the given name exists.
     *
     * @param roleName the name to check
     * @return true if a matching role exists
     */
    boolean existsByRoleName(String roleName);
}
