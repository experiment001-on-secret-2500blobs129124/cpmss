package com.cpmss.hr.staffsalaryhistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link StaffSalaryHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffSalaryHistoryRepository
        extends JpaRepository<StaffSalaryHistory, StaffSalaryHistoryId> {

    /**
     * Finds the current active salary row for a staff member.
     *
     * @param staffId the staff member UUID
     * @return the open-ended salary history row, if present
     */
    Optional<StaffSalaryHistory> findByStaffIdAndEndDateIsNull(UUID staffId);

    /**
     * Checks whether a salary row already exists for the same staff/date.
     *
     * @param staffId the staff member UUID
     * @param effectiveDate the effective date
     * @return true when a duplicate row exists
     */
    boolean existsByStaffIdAndEffectiveDate(UUID staffId, LocalDate effectiveDate);
}
