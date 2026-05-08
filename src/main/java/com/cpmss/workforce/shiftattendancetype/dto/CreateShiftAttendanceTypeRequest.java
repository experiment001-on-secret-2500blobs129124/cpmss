package com.cpmss.workforce.shiftattendancetype.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * Request payload for creating a new shift attendance type.
 *
 * @param shiftName the shift type's name
 */
public record CreateShiftAttendanceTypeRequest(
        @NotBlank @Size(max = 50) String shiftName
) {}
