package com.cpmss.workforce.attends;

import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.common.AttendanceTimeWindow;

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
     * @throws ApiException if no assignment exists
     */
    public void validateHasAssignedTask(boolean hasAssignment) {
        if (!hasAssignment) {
            throw new ApiException(WorkforceErrorCode.ATTENDANCE_DATE_OUTSIDE_TASK);
        }
    }

    /**
     * Validates that no duplicate attendance exists for the same staff+shift+date.
     *
     * @param exists whether an attendance record already exists
     * @throws ApiException if duplicate found
     */
    public void validateNoDuplicate(boolean exists) {
        if (exists) {
            throw new ApiException(WorkforceErrorCode.ATTENDANCE_DUPLICATE);
        }
    }

    /**
     * Validates that check-in and check-out times are provided when not absent.
     *
     * @param isAbsent    whether the staff member was absent
     * @param attendanceWindow actual attendance time window
     * @throws ApiException if present but missing times
     */
    public void validateTimesWhenPresent(boolean isAbsent, AttendanceTimeWindow attendanceWindow) {
        if (!isAbsent && attendanceWindow == null) {
            throw new ApiException(WorkforceErrorCode.ATTENDANCE_TIMES_REQUIRED);
        }
    }
}
