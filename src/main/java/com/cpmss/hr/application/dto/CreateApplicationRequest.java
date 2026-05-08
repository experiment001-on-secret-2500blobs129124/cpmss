package com.cpmss.hr.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for submitting a job application.
 *
 * @param applicantId     the person applying
 * @param positionId      the position applied for
 * @param applicationDate the date of the application
 */
public record CreateApplicationRequest(
        @NotNull UUID applicantId,
        @NotNull UUID positionId,
        @NotNull LocalDate applicationDate
) {}
