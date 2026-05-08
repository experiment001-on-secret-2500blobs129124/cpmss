package com.cpmss.hr.staffposition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link PositionSalaryHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 salary band lookups.
 */
public interface PositionSalaryHistoryRepository
        extends JpaRepository<PositionSalaryHistory, PositionSalaryHistoryId> {

    /**
     * Finds all salary history entries for a position, ordered by date descending.
     *
     * @param positionId the position's UUID
     * @return salary history entries, most recent first
     */
    List<PositionSalaryHistory> findByPositionIdOrderBySalaryEffectiveDateDesc(UUID positionId);
}
