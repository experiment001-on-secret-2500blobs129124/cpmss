package com.cpmss.people.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new role.
 *
 * @param roleName the role's name
 */
public record CreateRoleRequest(
        @NotBlank @Size(max = 50) String roleName
) {}
