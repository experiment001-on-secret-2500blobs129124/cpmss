package com.cpmss.identity.auth.dto;

import com.cpmss.identity.auth.SystemRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for an AppUser account.
 *
 * <p>Never includes the password hash — security sensitive fields
 * are excluded from all API responses.
 *
 * @param id                  the user's UUID primary key
 * @param email               the login email
 * @param systemRole          the assigned system role
 * @param active              whether the account is active
 * @param personId            linked Person UUID (may be {@code null})
 * @param forcePasswordChange whether the user must change password on next login
 * @param createdAt           when the account was created
 */
public record AppUserResponse(
        UUID id,
        String email,
        SystemRole systemRole,
        boolean active,
        UUID personId,
        boolean forcePasswordChange,
        Instant createdAt
) {}
