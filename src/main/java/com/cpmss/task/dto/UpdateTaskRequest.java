package com.cpmss.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for updating an existing task.
 *
 * @param taskTitle    the updated task title
 * @param departmentId the owning department's UUID
 */
public record UpdateTaskRequest(
        @NotBlank @Size(max = 50) String taskTitle,
        @NotNull UUID departmentId
) {}
