package com.cpmss.maintenance.workorder.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.maintenance.workorder.ServiceCategory;
import com.cpmss.maintenance.workorder.WorkOrderPriority;
import com.cpmss.maintenance.workorder.WorkOrderSchedule;
import com.cpmss.maintenance.workorder.WorkOrderStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload for a work order.
 *
 * @param id              the work order's UUID primary key
 * @param workOrderNo     system-unique work order number
 * @param schedule        optional scheduled/completed date pair
 * @param cost            cost money (may be {@code null})
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
        WorkOrderSchedule schedule,
        Money cost,
        WorkOrderStatus jobStatus,
        String description,
        WorkOrderPriority priority,
        ServiceCategory serviceCategory,
        UUID requesterId,
        UUID facilityId,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {}
