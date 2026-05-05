package com.cpmss.attends.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request payload for recording daily attendance.
 *
 * @param staffId      the staff member's person UUID
 * @param shiftId      the shift type UUID
 * @param date         the attendance date
 * @param isAbsent     whether the staff member was absent
 * @param checkInTime  check-in time (null if absent)
 * @param checkOutTime check-out time (null if absent)
 * @param periodOutIn  period out-in description
 * @param diffHour     hours difference from expected
 */
public record CreateAttendsRequest(
        @NotNull UUID staffId,
        @NotNull UUID shiftId,
        @NotNull LocalDate date,
        @NotNull Boolean isAbsent,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String periodOutIn,
        BigDecimal diffHour
) {}
