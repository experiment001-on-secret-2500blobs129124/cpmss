package com.cpmss.hr.staffprofile.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a staff profile.
 *
 * <p>The person must already exist and have the Staff role assigned.
 *
 * @param personId         the person's UUID (becomes the profile PK)
 * @param qualificationId  the qualification catalog entry UUID
 * @param qualificationDate date the qualification was obtained (may be {@code null})
 * @param cvFileUrl        URL to the uploaded CV in object storage (may be {@code null})
 */
public record CreateStaffProfileRequest(
        @NotNull UUID personId,
        @NotNull UUID qualificationId,
        LocalDate qualificationDate,
        @Size(max = 500) String cvFileUrl
) {}
