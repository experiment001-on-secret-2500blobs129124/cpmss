package com.cpmss.building;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Building} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Buildings are identified
 * by UUID and belong to a compound.
 */
public interface BuildingRepository extends JpaRepository<Building, UUID> {
}
