package com.cpmss.hr.lawofshiftattendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link LawOfShiftAttendance} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and effective-dated lookup helpers
 * used by shift attendance workflows.
 */
public interface LawOfShiftAttendanceRepository
        extends JpaRepository<LawOfShiftAttendance, LawOfShiftAttendanceId> {

    /**
     * Checks whether a law row already exists for the shift and date.
     *
     * @param shiftId       the shift type UUID
     * @param effectiveDate the law effective date
     * @return true when a row exists
     */
    boolean existsByShiftIdAndEffectiveDate(UUID shiftId, LocalDate effectiveDate);

    /**
     * Finds law rows attached to a shift type, newest first.
     *
     * @param shiftId the shift type UUID
     * @return shift attendance laws
     */
    List<LawOfShiftAttendance> findByShiftIdOrderByEffectiveDateDesc(UUID shiftId);

    /**
     * Finds the active law row for a shift as of a given date.
     *
     * @param shiftId the shift type UUID
     * @param asOf    the date used for effective-dated lookup
     * @return the newest law effective on or before the date
     */
    Optional<LawOfShiftAttendance> findFirstByShiftIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            UUID shiftId, LocalDate asOf);
}
