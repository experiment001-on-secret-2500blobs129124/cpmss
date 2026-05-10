package com.cpmss.organization.personsupervision.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for ending an active supervision relationship.
 *
 * @param supervisorId          the supervisor person UUID
 * @param superviseeId          the supervisee person UUID
 * @param supervisionStartDate  the supervision row start date
 * @param supervisionEndDate    the date supervision ends
 */
public record EndPersonSupervisionRequest(
        @NotNull UUID supervisorId,
        @NotNull UUID superviseeId,
        @NotNull LocalDate supervisionStartDate,
        @NotNull LocalDate supervisionEndDate
) {}
