package com.cpmss.security.entersat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload for recording a gate access event.
 *
 * <p>Exactly one of {@code permitId} or {@code manualPlateEntry}
 * must be set — enforced by {@link com.cpmss.security.entersat.EntersAtRules}.
 *
 * @param gateId           the gate where the event occurred
 * @param permitId         access permit used (may be {@code null} for anonymous)
 * @param manualPlateEntry manually entered plate (may be {@code null})
 * @param enteredAt        exact timestamp of the event
 * @param direction        direction of travel (In or Out)
 * @param purpose          purpose of the visit (optional)
 * @param processedById    guard who processed the event (optional)
 * @param requestedById    resident/staff the visitor is seeing (optional)
 */
public record CreateEntersAtRequest(
        @NotNull UUID gateId,
        UUID permitId,
        @Size(max = 20) String manualPlateEntry,
        @NotNull Instant enteredAt,
        @NotBlank @Size(max = 10) String direction,
        @Size(max = 100) String purpose,
        UUID processedById,
        UUID requestedById
) {}
