package com.cpmss.assignedtask.dto;

import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing task assignment.
 *
 * <p>Staff, task, shift, and date are immutable — only the duty
 * description can be updated.
 *
 * @param dutyDescription updated description of the specific duty
 */
public record UpdateAssignedTaskRequest(
        @Size(max = 200) String dutyDescription
) {}
