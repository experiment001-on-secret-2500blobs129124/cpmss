package com.cpmss.workforce.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a new task.
 *
 * @param taskTitle    the task's short title
 * @param departmentId the owning department's UUID
 */
public record CreateTaskRequest(
        @NotBlank @Size(max = 50) String taskTitle,
        @NotNull UUID departmentId
) {}
