package com.cpmss.workforce.shiftattendancetype;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.platform.exception.ApiException;
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
     * @throws ApiException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ApiException(WorkforceErrorCode.SHIFT_TYPE_DUPLICATE);
        }
    }
}
