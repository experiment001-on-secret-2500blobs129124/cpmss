package com.cpmss.shiftattendancetype;
import com.cpmss.exception.ConflictException;
/**
 * Business rules for {@link ShiftAttendanceType} operations.
 *
 * @see ShiftAttendanceTypeService
 */
public class ShiftAttendanceTypeRules {
    /**
     * Validates that a shift name is not already taken.
     *
     * @param name   the desired shift name
     * @param exists whether a shift type with this name already exists
     * @throws ConflictException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ConflictException("Shift attendance type '" + name + "' already exists");
        }
    }
}
