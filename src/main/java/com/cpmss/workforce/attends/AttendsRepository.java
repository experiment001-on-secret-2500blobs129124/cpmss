package com.cpmss.workforce.attends;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link Attends} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for attendance lookups by staff and date range.
 */
public interface AttendsRepository extends JpaRepository<Attends, AttendsId> {

    /**
     * Finds all attendance records for a staff member in a date range.
     *
     * @param staffId  the staff member's person UUID
     * @param fromDate start date (inclusive)
     * @param toDate   end date (inclusive)
     * @return attendance records in the range
     */
    List<Attends> findByStaffIdAndDateBetween(UUID staffId, LocalDate fromDate, LocalDate toDate);

    /**
     * Finds all attendance records for a staff member on a specific shift
     * within a date range (used for monthly payroll rollup).
     *
     * @param staffId  the staff member's person UUID
     * @param shiftId  the shift type UUID
     * @param fromDate start date (inclusive)
     * @param toDate   end date (inclusive)
     * @return attendance records matching the criteria
     */
    List<Attends> findByStaffIdAndShiftIdAndDateBetween(
            UUID staffId, UUID shiftId, LocalDate fromDate, LocalDate toDate);
}
