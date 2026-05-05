package com.cpmss.workorder.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a work order.
 *
 * @param id              the work order's UUID primary key
 * @param workOrderNo     system-unique work order number
 * @param dateScheduled   scheduled date (may be {@code null})
 * @param dateCompleted   completion date (may be {@code null})
 * @param costAmount      cost (may be {@code null})
 * @param jobStatus       lifecycle status
 * @param description     description (may be {@code null})
 * @param priority        priority level (may be {@code null})
 * @param serviceCategory service category (may be {@code null})
 * @param requesterId     person who raised the work order
 * @param facilityId      related facility (may be {@code null})
 * @param companyId       assigned company (may be {@code null})
 * @param createdAt       when the work order was created
 * @param updatedAt       when the work order was last modified
 */
public record WorkOrderResponse(
        UUID id,
        String workOrderNo,
        LocalDate dateScheduled,
        LocalDate dateCompleted,
        BigDecimal costAmount,
        String jobStatus,
        String description,
        String priority,
        String serviceCategory,
        UUID requesterId,
        UUID facilityId,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {}
