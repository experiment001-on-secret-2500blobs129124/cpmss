package com.cpmss.property.compound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Compound} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. No custom query methods
 * are needed — compounds are identified solely by UUID.
 */
public interface CompoundRepository extends JpaRepository<Compound, UUID> {
}
