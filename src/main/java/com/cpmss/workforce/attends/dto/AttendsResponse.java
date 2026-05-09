package com.cpmss.workforce.attends.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.workforce.common.AttendanceTimeWindow;
import com.cpmss.workforce.common.HourDelta;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for an attendance record.
 *
 * @param staffId        the staff member's person UUID
 * @param shiftId        the shift type UUID
 * @param date           the attendance date
 * @param isAbsent       whether the staff member was absent
 * @param attendanceWindow actual check-in/check-out window
 * @param periodOutIn    period out-in description
 * @param diffHour       hours difference from expected
 * @param dailyBonus     computed daily bonus money
 * @param dailyDeduction computed daily deduction money
 * @param dailySalary    computed daily salary money
 * @param dailyNetSalary computed daily net salary money
 */
public record AttendsResponse(
        UUID staffId,
        UUID shiftId,
        LocalDate date,
        Boolean isAbsent,
        AttendanceTimeWindow attendanceWindow,
        String periodOutIn,
        HourDelta diffHour,
        Money dailyBonus,
        Money dailyDeduction,
        Money dailySalary,
        Money dailyNetSalary
) {}
