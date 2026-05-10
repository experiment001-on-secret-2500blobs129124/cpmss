package com.cpmss.organization.personsupervision.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a supervision relationship.
 *
 * @param supervisorId          the supervisor person UUID
 * @param superviseeId          the supervisee person UUID
 * @param supervisionStartDate  the date supervision starts
 * @param teamName              optional team label
 */
public record CreatePersonSupervisionRequest(
        @NotNull UUID supervisorId,
        @NotNull UUID superviseeId,
        @NotNull LocalDate supervisionStartDate,
        @Size(max = 100) String teamName
) {}
