package com.cpmss.hr.staffpositionhistory.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for assigning a staff member to a position.
 *
 * @param personId       the staff member
 * @param positionId     the new position
 * @param effectiveDate  the date the assignment starts
 * @param authorizedById the manager or HR officer authorizing the change
 */
public record CreateStaffPositionHistoryRequest(
        @NotNull UUID personId,
        @NotNull UUID positionId,
        @NotNull LocalDate effectiveDate,
        UUID authorizedById
) {}
