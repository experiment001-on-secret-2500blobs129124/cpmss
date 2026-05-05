package com.cpmss.auth.dto;

import com.cpmss.auth.SystemRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for changing a user's system role.
 *
 * @param systemRole the new system role to assign
 */
public record UpdateUserRoleRequest(
        @NotNull SystemRole systemRole
) {}
