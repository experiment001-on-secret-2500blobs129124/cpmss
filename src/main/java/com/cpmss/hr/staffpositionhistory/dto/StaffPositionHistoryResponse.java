package com.cpmss.hr.staffpositionhistory.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for staff position history rows.
 *
 * @param personId       the staff member
 * @param positionId     the position held
 * @param effectiveDate  the date this assignment starts
 * @param endDate        the date this assignment ends, or {@code null} when current
 * @param authorizedById the authorizer, or {@code null} for initial hiring
 */
public record StaffPositionHistoryResponse(
        UUID personId,
        UUID positionId,
        LocalDate effectiveDate,
        LocalDate endDate,
        UUID authorizedById
) {}
