package com.cpmss.unitstatushistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link UnitStatusHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 unit status lookups.
 */
public interface UnitStatusHistoryRepository
        extends JpaRepository<UnitStatusHistory, UnitStatusHistoryId> {

    /**
     * Finds all status history for a unit, ordered by date descending.
     *
     * @param unitId the unit's UUID
     * @return status history entries, most recent first
     */
    List<UnitStatusHistory> findByUnitIdOrderByEffectiveDateDesc(UUID unitId);
}
