package com.cpmss.staffprofile.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a staff profile.
 *
 * @param personId          the person's UUID (also the profile PK)
 * @param personFirstName   the person's first name
 * @param personLastName    the person's last name
 * @param qualificationId   the qualification catalog entry UUID
 * @param qualificationName the qualification display name
 * @param qualificationDate date the qualification was obtained (may be {@code null})
 * @param cvFileUrl         URL to the CV in object storage (may be {@code null})
 * @param createdAt         when the profile was created
 * @param updatedAt         when the profile was last modified
 */
public record StaffProfileResponse(
        UUID personId,
        String personFirstName,
        String personLastName,
        UUID qualificationId,
        String qualificationName,
        LocalDate qualificationDate,
        String cvFileUrl,
        Instant createdAt,
        Instant updatedAt
) {}
