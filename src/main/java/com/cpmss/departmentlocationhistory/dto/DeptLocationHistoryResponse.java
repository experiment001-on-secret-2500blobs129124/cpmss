package com.cpmss.departmentlocationhistory.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a department location history entry.
 *
 * @param departmentId       the department UUID
 * @param locationStartDate  the date this location became active
 * @param locationEndDate    the date this location ended ({@code null} = current)
 * @param buildingId         the building UUID
 */
public record DeptLocationHistoryResponse(
        UUID departmentId,
        LocalDate locationStartDate,
        LocalDate locationEndDate,
        UUID buildingId
) {}
