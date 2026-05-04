package com.cpmss.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link AppUser} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query methods
 * for authentication.
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    /**
     * Finds an active user by email.
     *
     * @param email the login email
     * @return the matching user, or empty
     */
    Optional<AppUser> findByEmailAndActiveTrue(String email);

    /**
     * Checks whether any user exists in the system.
     *
     * @return true if at least one user row exists
     */
    boolean existsByEmailIgnoreCase(String email);
}
