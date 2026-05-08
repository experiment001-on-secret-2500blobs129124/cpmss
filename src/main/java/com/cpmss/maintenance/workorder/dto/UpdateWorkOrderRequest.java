package com.cpmss.maintenance.workorder.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.maintenance.workorder.ServiceCategory;
import com.cpmss.maintenance.workorder.WorkOrderPriority;
import com.cpmss.maintenance.workorder.WorkOrderSchedule;
import com.cpmss.maintenance.workorder.WorkOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for updating an existing work order.
 *
 * @param schedule        optional scheduled/completed date pair
 * @param cost            optional cost money
 * @param jobStatus       lifecycle status
 * @param description     detailed description of the work
 * @param priority        priority level
 * @param serviceCategory service category
 * @param facilityId      related facility (optional)
 * @param companyId       assigned company (optional)
 */
public record UpdateWorkOrderRequest(
        @Valid WorkOrderSchedule schedule,
        @Valid Money cost,
        @NotNull WorkOrderStatus jobStatus,
        String description,
        WorkOrderPriority priority,
        ServiceCategory serviceCategory,
        UUID facilityId,
        UUID companyId
) {}
