package com.cpmss.property.facility;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Facility} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Facilities are
 * identified by UUID and belong to a building.
 */
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
}
