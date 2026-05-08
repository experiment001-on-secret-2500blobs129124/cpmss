package com.cpmss.security.accesspermit.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for an access permit.
 *
 * @param id             the permit's UUID primary key
 * @param permitNo       system-unique permit number
 * @param permitType     type (Staff Badge, Resident Card, etc.)
 * @param accessLevel    access level (may be {@code null})
 * @param permitStatus   lifecycle status
 * @param issueDate      date the permit was issued
 * @param expiryDate     expiry date (may be {@code null})
 * @param permitHolderId person holding the permit
 * @param staffProfileId staff profile entitlement (may be {@code null})
 * @param contractId     contract entitlement (may be {@code null})
 * @param workOrderId    work order entitlement (may be {@code null})
 * @param invitedById    inviting person (may be {@code null})
 * @param issuedById     staff who issued the permit
 * @param createdAt      when the permit was created
 * @param updatedAt      when the permit was last modified
 */
public record AccessPermitResponse(
        UUID id,
        String permitNo,
        String permitType,
        String accessLevel,
        String permitStatus,
        LocalDate issueDate,
        LocalDate expiryDate,
        UUID permitHolderId,
        UUID staffProfileId,
        UUID contractId,
        UUID workOrderId,
        UUID invitedById,
        UUID issuedById,
        Instant createdAt,
        Instant updatedAt
) {}
