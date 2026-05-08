package com.cpmss.maintenance.workorder;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.maintenance.workorder.dto.CreateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.UpdateWorkOrderRequest;
import com.cpmss.maintenance.workorder.dto.WorkOrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for work order CRUD operations.
 *
 * <p>No delete endpoint — work orders are permanent records.
 *
 * @see WorkOrderService
 */
@RestController
public class WorkOrderApiController {

    private final WorkOrderService workOrderService;

    /**
     * Constructs the controller with the work order service.
     *
     * @param workOrderService work order orchestration service
     */
    public WorkOrderApiController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /** Lists all work orders with pagination. */
    @GetMapping(ApiPaths.WORK_ORDERS)
    public ResponseEntity<ApiResponse<PagedResponse<WorkOrderResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(workOrderService.listAll(pageable)));
    }

    /** Retrieves a single work order by ID. */
    @GetMapping(ApiPaths.WORK_ORDERS_BY_ID)
    public ResponseEntity<ApiResponse<WorkOrderResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workOrderService.getById(id)));
    }

    /** Creates a new work order. */
    @PostMapping(ApiPaths.WORK_ORDERS)
    public ResponseEntity<ApiResponse<WorkOrderResponse>> create(
            @Valid @RequestBody CreateWorkOrderRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(workOrderService.create(request)));
    }

    /** Updates an existing work order. */
    @PutMapping(ApiPaths.WORK_ORDERS_BY_ID)
    public ResponseEntity<ApiResponse<WorkOrderResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(workOrderService.update(id, request)));
    }
}
