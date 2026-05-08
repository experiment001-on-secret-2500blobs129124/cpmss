package com.cpmss.identity.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for the first-admin bootstrap endpoint.
 *
 * @param email    the admin's login email
 * @param password the initial password (will be BCrypt-hashed)
 */
public record SetupRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {}
