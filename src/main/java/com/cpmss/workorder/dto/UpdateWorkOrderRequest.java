package com.cpmss.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for updating an existing work order.
 *
 * @param dateScheduled   scheduled date for the work
 * @param dateCompleted   completion date
 * @param costAmount      cost of the work
 * @param jobStatus       lifecycle status
 * @param description     detailed description of the work
 * @param priority        priority level
 * @param serviceCategory service category
 * @param facilityId      related facility (optional)
 * @param companyId       assigned company (optional)
 */
public record UpdateWorkOrderRequest(
        LocalDate dateScheduled,
        LocalDate dateCompleted,
        BigDecimal costAmount,
        @NotBlank @Size(max = 50) String jobStatus,
        String description,
        @Size(max = 20) String priority,
        @Size(max = 50) String serviceCategory,
        UUID facilityId,
        UUID companyId
) {}
