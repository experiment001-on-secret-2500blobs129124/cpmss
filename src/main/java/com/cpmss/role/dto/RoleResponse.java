package com.cpmss.role.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a role.
 *
 * @param id        the role UUID
 * @param roleName  the role's name
 * @param createdAt when the role was created
 * @param updatedAt when the role was last modified
 */
public record RoleResponse(
        UUID id,
        String roleName,
        Instant createdAt,
        Instant updatedAt
) {}
