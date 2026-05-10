package com.cpmss.hr.staffpositionhistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
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

    /**
     * Finds the current active position assignment for a staff member.
     *
     * @param personId the person UUID
     * @return the current open-ended assignment, if present
     */
    Optional<StaffPositionHistory> findByPersonIdAndEndDateIsNull(UUID personId);

    /**
     * Checks whether an assignment already exists for the same composite key.
     *
     * @param personId the staff member UUID
     * @param positionId the position UUID
     * @param effectiveDate the effective date
     * @return true when the assignment already exists
     */
    boolean existsByPersonIdAndPositionIdAndEffectiveDate(
            UUID personId, UUID positionId, java.time.LocalDate effectiveDate);
}
