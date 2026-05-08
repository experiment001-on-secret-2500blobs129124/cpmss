package com.cpmss.identity.auth.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload for activating or deactivating a user account.
 *
 * @param active whether the account should be active
 */
public record UpdateUserStatusRequest(
        @NotNull Boolean active
) {}
