package com.cpmss.security.accesspermit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating an access permit.
 *
 * <p>Exactly one entitlement basis must be set — enforced by
 * {@link com.cpmss.security.accesspermit.AccessPermitRules}.
 *
 * @param permitNo       system-unique permit number
 * @param permitType     type (Staff Badge, Resident Card, Contractor Pass, Visitor Pass)
 * @param accessLevel    access level (Full, Restricted, Emergency Only)
 * @param permitStatus   lifecycle status (Active, Suspended, Revoked, Expired)
 * @param issueDate      date the permit was issued
 * @param expiryDate     date the permit expires ({@code null} = no expiry)
 * @param permitHolderId the person holding this permit
 * @param staffProfileId staff profile entitlement (may be {@code null})
 * @param contractId     contract entitlement (may be {@code null})
 * @param workOrderId    work order entitlement (may be {@code null})
 * @param invitedById    inviting person entitlement (may be {@code null})
 * @param issuedById     the staff member who created the permit
 */
public record CreateAccessPermitRequest(
        @NotBlank @Size(max = 20) String permitNo,
        @NotBlank @Size(max = 50) String permitType,
        @Size(max = 50) String accessLevel,
        @NotBlank @Size(max = 50) String permitStatus,
        @NotNull LocalDate issueDate,
        LocalDate expiryDate,
        @NotNull UUID permitHolderId,
        UUID staffProfileId,
        UUID contractId,
        UUID workOrderId,
        UUID invitedById,
        @NotNull UUID issuedById
) {}
