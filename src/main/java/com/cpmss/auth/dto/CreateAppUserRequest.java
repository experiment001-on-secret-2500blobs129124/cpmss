package com.cpmss.auth.dto;

import com.cpmss.auth.SystemRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a new AppUser account.
 *
 * <p>Used by ADMIN, GENERAL_MANAGER, HR_OFFICER, and DEPARTMENT_MANAGER
 * to provision login accounts. Authority checks are enforced in
 * {@link com.cpmss.auth.AppUserRules}.
 *
 * @param email      the user's login email
 * @param password   the initial password (will be BCrypt-hashed)
 * @param systemRole the system role to assign
 * @param personId   optional link to an existing Person record
 */
public record CreateAppUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull SystemRole systemRole,
        UUID personId
) {}
