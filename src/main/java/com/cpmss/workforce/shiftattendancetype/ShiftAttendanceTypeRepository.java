package com.cpmss.workforce.shiftattendancetype;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/**
 * Spring Data repository for {@link ShiftAttendanceType} entities.
 */
public interface ShiftAttendanceTypeRepository extends JpaRepository<ShiftAttendanceType, UUID> {
    /**
     * Checks whether a shift type with the given name exists.
     *
     * @param shiftName the name to check
     * @return true if a matching shift type exists
     */
    boolean existsByShiftName(String shiftName);
}
