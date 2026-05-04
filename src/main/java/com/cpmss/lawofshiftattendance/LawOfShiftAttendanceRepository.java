package com.cpmss.lawofshiftattendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link LawOfShiftAttendance} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 shift rule lookups.
 */
public interface LawOfShiftAttendanceRepository
        extends JpaRepository<LawOfShiftAttendance, LawOfShiftAttendanceId> {

    /**
     * Finds all shift rules for a given shift type, ordered by effective date descending.
     *
     * @param shiftId the shift type's UUID
     * @return shift rules, most recent first
     */
    List<LawOfShiftAttendance> findByShiftIdOrderByEffectiveDateDesc(UUID shiftId);
}
