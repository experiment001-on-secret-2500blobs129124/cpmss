package com.cpmss.unitpricinghistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link UnitPricingHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 listing price lookups.
 */
public interface UnitPricingHistoryRepository
        extends JpaRepository<UnitPricingHistory, UnitPricingHistoryId> {

    /**
     * Finds all pricing history entries for a unit, ordered by date descending.
     *
     * @param unitId the unit's UUID
     * @return pricing history entries, most recent first
     */
    List<UnitPricingHistory> findByUnitIdOrderByEffectiveDateDesc(UUID unitId);
}
