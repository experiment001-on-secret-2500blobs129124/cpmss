package com.cpmss.maintenance.workorder.dto;

import com.cpmss.finance.money.Money;
import com.cpmss.maintenance.workorder.ServiceCategory;
import com.cpmss.maintenance.workorder.WorkOrderPriority;
import com.cpmss.maintenance.workorder.WorkOrderSchedule;
import com.cpmss.maintenance.workorder.WorkOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating a work order.
 *
 * @param workOrderNo     system-unique work order number
 * @param schedule        optional scheduled/completed date pair
 * @param cost            optional cost money
 * @param jobStatus       lifecycle status (Pending, Assigned, In Progress, Completed, Paid, Cancelled)
 * @param description     detailed description of the work (optional)
 * @param priority        priority level (Low, Normal, High, Emergency)
 * @param serviceCategory service category (Plumbing, Electrical, etc.)
 * @param requesterId     the person who raised the work order
 * @param facilityId      related facility (optional)
 * @param companyId       assigned company (optional)
 */
public record CreateWorkOrderRequest(
        @NotBlank @Size(max = 20) String workOrderNo,
        @Valid WorkOrderSchedule schedule,
        @Valid Money cost,
        @NotNull WorkOrderStatus jobStatus,
        String description,
        WorkOrderPriority priority,
        ServiceCategory serviceCategory,
        @NotNull UUID requesterId,
        UUID facilityId,
        UUID companyId
) {}
