package com.cpmss.attends;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link Attends} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * attendance lookups by staff and date range.
 */
public interface AttendsRepository extends JpaRepository<Attends, AttendsId> {

    /**
     * Find all attendance records for a staff member in a date range.
     *
     * @param staffId  the staff member's person UUID
     * @param fromDate start date (inclusive)
     * @param toDate   end date (inclusive)
     * @return attendance records in the range
     */
    List<Attends> findByStaffIdAndDateBetween(UUID staffId, LocalDate fromDate, LocalDate toDate);

    /**
     * Find all attendance records for a shift in a date range (for monthly rollup).
     *
     * @param staffId  the staff member's person UUID
     * @param shiftId  the shift type UUID
     * @param fromDate start date (inclusive)
     * @param toDate   end date (inclusive)
     * @return attendance records matching
     */
    List<Attends> findByStaffIdAndShiftIdAndDateBetween(
            UUID staffId, UUID shiftId, LocalDate fromDate, LocalDate toDate);
}
