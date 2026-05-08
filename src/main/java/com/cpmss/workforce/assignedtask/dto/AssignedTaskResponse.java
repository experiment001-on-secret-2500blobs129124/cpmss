package com.cpmss.workforce.assignedtask.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a task assignment.
 *
 * @param id              the assignment's UUID primary key
 * @param staffId         the assigned staff member UUID
 * @param taskId          the task type UUID
 * @param shiftId         the shift type UUID
 * @param assignmentDate  the date of the assignment
 * @param dutyDescription duty description (may be {@code null})
 * @param createdAt       when the assignment was created
 * @param updatedAt       when the assignment was last modified
 */
public record AssignedTaskResponse(
        UUID id,
        UUID staffId,
        UUID taskId,
        UUID shiftId,
        LocalDate assignmentDate,
        String dutyDescription,
        Instant createdAt,
        Instant updatedAt
) {}
