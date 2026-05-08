package com.cpmss.maintenance.workorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a work order.
 *
 * @param workOrderNo     system-unique work order number
 * @param dateScheduled   scheduled date for the work (optional)
 * @param dateCompleted   completion date (optional)
 * @param costAmount      cost of the work (optional)
 * @param jobStatus       lifecycle status (Open, In Progress, Completed, Cancelled)
 * @param description     detailed description of the work (optional)
 * @param priority        priority level (Low, Medium, High, Critical)
 * @param serviceCategory service category (Plumbing, Electrical, etc.)
 * @param requesterId     the person who raised the work order
 * @param facilityId      related facility (optional)
 * @param companyId       assigned company (optional)
 */
public record CreateWorkOrderRequest(
        @NotBlank @Size(max = 20) String workOrderNo,
        LocalDate dateScheduled,
        LocalDate dateCompleted,
        BigDecimal costAmount,
        @NotBlank @Size(max = 50) String jobStatus,
        String description,
        @Size(max = 20) String priority,
        @Size(max = 50) String serviceCategory,
        @NotNull UUID requesterId,
        UUID facilityId,
        UUID companyId
) {}
