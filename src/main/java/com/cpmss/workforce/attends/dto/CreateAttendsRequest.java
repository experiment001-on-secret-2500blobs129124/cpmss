package com.cpmss.workforce.attends.dto;

import com.cpmss.workforce.common.AttendanceTimeWindow;
import com.cpmss.workforce.common.HourDelta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for recording daily attendance.
 *
 * @param staffId      the staff member's person UUID
 * @param shiftId      the shift type UUID
 * @param date         the attendance date
 * @param isAbsent     whether the staff member was absent
 * @param attendanceWindow actual check-in/check-out window (null if absent)
 * @param periodOutIn  period out-in description
 * @param diffHour     hours difference from expected
 */
public record CreateAttendsRequest(
        @NotNull UUID staffId,
        @NotNull UUID shiftId,
        @NotNull LocalDate date,
        @NotNull Boolean isAbsent,
        @Valid AttendanceTimeWindow attendanceWindow,
        String periodOutIn,
        HourDelta diffHour
) {}
