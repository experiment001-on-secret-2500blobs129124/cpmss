package com.cpmss.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for refreshing an access token.
 *
 * @param refreshToken the valid refresh JWT
 */
public record RefreshRequest(
        @NotBlank String refreshToken
) {}
