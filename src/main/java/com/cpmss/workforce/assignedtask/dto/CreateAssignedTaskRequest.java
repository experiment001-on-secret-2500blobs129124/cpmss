package com.cpmss.workforce.assignedtask.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a task assignment.
 *
 * @param staffId         the staff member UUID
 * @param taskId          the task type UUID
 * @param shiftId         the shift type UUID
 * @param assignmentDate  the date of the assignment
 * @param dutyDescription optional description of the specific duty
 */
public record CreateAssignedTaskRequest(
        @NotNull UUID staffId,
        @NotNull UUID taskId,
        @NotNull UUID shiftId,
        @NotNull LocalDate assignmentDate,
        @Size(max = 200) String dutyDescription
) {}
