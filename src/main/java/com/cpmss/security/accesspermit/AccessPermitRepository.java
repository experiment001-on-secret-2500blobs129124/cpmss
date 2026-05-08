package com.cpmss.security.accesspermit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link AccessPermit} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface AccessPermitRepository extends JpaRepository<AccessPermit, UUID> {
}
