package com.cpmss.accesspermit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for updating an existing access permit.
 *
 * <p>Entitlement basis and holder are immutable after creation.
 * Only status, access level, and expiry can be changed.
 *
 * @param accessLevel  access level (Full, Restricted, Emergency Only)
 * @param permitStatus lifecycle status (Active, Suspended, Revoked, Expired)
 * @param expiryDate   date the permit expires ({@code null} = no expiry)
 */
public record UpdateAccessPermitRequest(
        @Size(max = 50) String accessLevel,
        @NotBlank @Size(max = 50) String permitStatus,
        LocalDate expiryDate
) {}
