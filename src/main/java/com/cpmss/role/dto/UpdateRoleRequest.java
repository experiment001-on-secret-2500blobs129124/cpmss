package com.cpmss.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing role.
 *
 * @param roleName the updated role name
 */
public record UpdateRoleRequest(
        @NotBlank @Size(max = 50) String roleName
) {}
