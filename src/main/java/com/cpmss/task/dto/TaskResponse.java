package com.cpmss.task.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a task.
 *
 * @param id             the task UUID
 * @param taskTitle      the task's short title
 * @param departmentId   the owning department's UUID
 * @param departmentName the owning department's name (denormalized for display)
 * @param createdAt      when the task was created
 * @param updatedAt      when the task was last modified
 */
public record TaskResponse(
        UUID id,
        String taskTitle,
        UUID departmentId,
        String departmentName,
        Instant createdAt,
        Instant updatedAt
) {}
