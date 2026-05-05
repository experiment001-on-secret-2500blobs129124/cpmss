package com.cpmss.departmentmanagers.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a department manager assignment.
 *
 * @param departmentId        the department UUID
 * @param managerId           the manager's person UUID
 * @param managementStartDate the assignment start date
 * @param managementEndDate   the assignment end date ({@code null} = still active)
 */
public record DeptManagerResponse(
        UUID departmentId,
        UUID managerId,
        LocalDate managementStartDate,
        LocalDate managementEndDate
) {}
