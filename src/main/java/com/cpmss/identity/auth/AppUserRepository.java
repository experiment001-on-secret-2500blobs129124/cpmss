package com.cpmss.identity.auth;

import com.cpmss.people.common.EmailAddress;
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
     * @param email the normalized login email
     * @return the matching user, or empty
     */
    Optional<AppUser> findByEmailAndActiveTrue(EmailAddress email);

    /**
     * Checks whether a user exists by normalized login email.
     *
     * @param email the normalized login email
     * @return true if a matching user exists
     */
    boolean existsByEmail(EmailAddress email);
}
