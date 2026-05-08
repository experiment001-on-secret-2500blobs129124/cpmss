package com.cpmss.workforce.shiftattendancetype.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * Request payload for updating an existing shift attendance type.
 *
 * @param shiftName the updated shift type name
 */
public record UpdateShiftAttendanceTypeRequest(
        @NotBlank @Size(max = 50) String shiftName
) {}
