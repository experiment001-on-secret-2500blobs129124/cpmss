package com.cpmss.workforce.attends;

import com.cpmss.platform.exception.BusinessException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Stateless business rules for attendance recording.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Staff must have an AssignedTask for the given date</li>
 *   <li>No duplicate attendance for same staff+shift+date</li>
 *   <li>Check-in/out times required when not absent</li>
 * </ul>
 */
public class AttendsRules {

    /**
     * Validates that a staff member has an assigned task before recording attendance.
     *
     * @param hasAssignment whether the staff has an assigned task
     * @throws BusinessException if no assignment exists
     */
    public void validateHasAssignedTask(boolean hasAssignment) {
        if (!hasAssignment) {
            throw new BusinessException(
                    "Cannot record attendance — staff has no assigned task for this date");
        }
    }

    /**
     * Validates that no duplicate attendance exists for the same staff+shift+date.
     *
     * @param exists whether an attendance record already exists
     * @throws BusinessException if duplicate found
     */
    public void validateNoDuplicate(boolean exists) {
        if (exists) {
            throw new BusinessException(
                    "Attendance record already exists for this staff, shift, and date");
        }
    }

    /**
     * Validates that check-in and check-out times are provided when not absent.
     *
     * @param isAbsent    whether the staff member was absent
     * @param hasCheckIn  whether check-in time is provided
     * @param hasCheckOut whether check-out time is provided
     * @throws BusinessException if present but missing times
     */
    public void validateTimesWhenPresent(boolean isAbsent, boolean hasCheckIn, boolean hasCheckOut) {
        if (!isAbsent && (!hasCheckIn || !hasCheckOut)) {
            throw new BusinessException(
                    "Check-in and check-out times are required when staff is not absent");
        }
    }
}
