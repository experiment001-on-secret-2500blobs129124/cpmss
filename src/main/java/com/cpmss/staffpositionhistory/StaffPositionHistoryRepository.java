package com.cpmss.staffpositionhistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffPositionHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 position assignment lookups.
 */
public interface StaffPositionHistoryRepository
        extends JpaRepository<StaffPositionHistory, StaffPositionHistoryId> {

    /**
     * Finds all position history entries for a person, ordered by date descending.
     *
     * @param personId the person's UUID
     * @return position history entries, most recent first
     */
    List<StaffPositionHistory> findByPersonIdOrderByEffectiveDateDesc(UUID personId);
}
