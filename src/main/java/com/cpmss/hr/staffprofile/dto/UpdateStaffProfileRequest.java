package com.cpmss.hr.staffprofile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for updating a staff profile.
 *
 * @param qualificationId  the updated qualification catalog entry UUID
 * @param qualificationDate updated date the qualification was obtained (may be {@code null})
 * @param cvFileUrl        updated URL to the CV in object storage (may be {@code null})
 */
public record UpdateStaffProfileRequest(
        @NotNull UUID qualificationId,
        LocalDate qualificationDate,
        @Size(max = 500) String cvFileUrl
) {}
