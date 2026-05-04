package com.cpmss.entersat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link EntersAt} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface EntersAtRepository extends JpaRepository<EntersAt, UUID> {
}
