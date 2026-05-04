package com.cpmss.department.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a department.
 *
 * @param id             the department UUID
 * @param departmentName the department's name
 * @param createdAt      when the department was created
 * @param updatedAt      when the department was last modified
 */
public record DepartmentResponse(
        UUID id,
        String departmentName,
        Instant createdAt,
        Instant updatedAt
) {}
