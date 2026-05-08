package com.cpmss.property.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Unit} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Includes a check
 * for unit number uniqueness within a building.
 */
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    /**
     * Checks whether a unit with the given number exists in a building.
     *
     * @param unitNo     the unit number to check
     * @param buildingId the building UUID
     * @return true if a matching unit exists
     */
    boolean existsByUnitNoAndBuildingId(String unitNo, UUID buildingId);
}
