package com.cpmss.hr.staffposition.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a staff position.
 *
 * @param id             the position's UUID primary key
 * @param positionName   the position title
 * @param departmentId   the owning department's UUID
 * @param departmentName the owning department's name
 * @param createdAt      when the position was created
 * @param updatedAt      when the position was last modified
 */
public record StaffPositionResponse(
        UUID id,
        String positionName,
        UUID departmentId,
        String departmentName,
        Instant createdAt,
        Instant updatedAt
) {}
