package com.cpmss.maintenance.workorderassignedto.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for assigning a vendor company to a work order.
 *
 * @param companyId    the assigned company UUID
 * @param dateAssigned the assignment date
 */
public record AssignWorkOrderCompanyRequest(
        @NotNull UUID companyId,
        @NotNull LocalDate dateAssigned
) {}
