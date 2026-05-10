package com.cpmss.organization.personsupervision.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a supervision relationship.
 *
 * @param supervisorId          the supervisor person UUID
 * @param superviseeId          the supervisee person UUID
 * @param supervisionStartDate  the date supervision starts
 * @param supervisionEndDate    the date supervision ends, or null when active
 * @param teamName              optional team label
 */
public record PersonSupervisionResponse(
        UUID supervisorId,
        UUID superviseeId,
        LocalDate supervisionStartDate,
        LocalDate supervisionEndDate,
        String teamName
) {}
