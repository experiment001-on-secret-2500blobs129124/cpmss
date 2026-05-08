package com.cpmss.workforce.attends.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response payload for an attendance record.
 *
 * @param staffId        the staff member's person UUID
 * @param shiftId        the shift type UUID
 * @param date           the attendance date
 * @param isAbsent       whether the staff member was absent
 * @param checkInTime    check-in time
 * @param checkOutTime   check-out time
 * @param periodOutIn    period out-in description
 * @param diffHour       hours difference from expected
 * @param dailyBonus     computed daily bonus
 * @param dailyDeduction computed daily deduction
 * @param dailySalary    computed daily salary
 * @param dailyNetSalary computed daily net salary
 */
public record AttendsResponse(
        UUID staffId,
        UUID shiftId,
        LocalDate date,
        Boolean isAbsent,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String periodOutIn,
        BigDecimal diffHour,
        BigDecimal dailyBonus,
        BigDecimal dailyDeduction,
        BigDecimal dailySalary,
        BigDecimal dailyNetSalary
) {}
