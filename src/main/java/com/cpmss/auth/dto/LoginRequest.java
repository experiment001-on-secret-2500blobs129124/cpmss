package com.cpmss.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for user login.
 *
 * @param email    the user's login email
 * @param password the user's plain-text password
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
