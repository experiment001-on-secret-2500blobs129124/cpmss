package com.cpmss.maintenance.workorderassignedto.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a work-order vendor assignment.
 *
 * @param workOrderId   the work order UUID
 * @param companyId     the assigned company UUID
 * @param dateAssigned  the assignment date
 */
public record WorkOrderAssignmentResponse(
        UUID workOrderId,
        UUID companyId,
        LocalDate dateAssigned
) {}
