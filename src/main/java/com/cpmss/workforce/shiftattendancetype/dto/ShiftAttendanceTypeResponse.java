package com.cpmss.workforce.shiftattendancetype.dto;
import java.time.Instant;
import java.util.UUID;
/**
 * Response payload for a shift attendance type.
 *
 * @param id        the shift type UUID
 * @param shiftName the shift type's name
 * @param createdAt when the shift type was created
 * @param updatedAt when the shift type was last modified
 */
public record ShiftAttendanceTypeResponse(
        UUID id,
        String shiftName,
        Instant createdAt,
        Instant updatedAt
) {}
