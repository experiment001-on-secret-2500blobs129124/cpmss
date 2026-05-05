package com.cpmss.assignedtask;

import com.cpmss.assignedtask.dto.AssignedTaskResponse;
import com.cpmss.assignedtask.dto.CreateAssignedTaskRequest;
import com.cpmss.assignedtask.dto.UpdateAssignedTaskRequest;
import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
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
 * REST controller for task assignment CRUD operations.
 *
 * @see AssignedTaskService
 */
@RestController
public class AssignedTaskApiController {

    private final AssignedTaskService assignedTaskService;

    /**
     * Constructs the controller with the assigned task service.
     *
     * @param assignedTaskService task assignment orchestration service
     */
    public AssignedTaskApiController(AssignedTaskService assignedTaskService) {
        this.assignedTaskService = assignedTaskService;
    }

    /** Lists all task assignments with pagination. */
    @GetMapping(ApiPaths.ASSIGNED_TASKS)
    public ResponseEntity<ApiResponse<PagedResponse<AssignedTaskResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(assignedTaskService.listAll(pageable)));
    }

    /** Retrieves a single task assignment by ID. */
    @GetMapping(ApiPaths.ASSIGNED_TASKS_BY_ID)
    public ResponseEntity<ApiResponse<AssignedTaskResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(assignedTaskService.getById(id)));
    }

    /** Creates a new task assignment. */
    @PostMapping(ApiPaths.ASSIGNED_TASKS)
    public ResponseEntity<ApiResponse<AssignedTaskResponse>> create(
            @Valid @RequestBody CreateAssignedTaskRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(assignedTaskService.create(request)));
    }

    /** Updates an existing task assignment. */
    @PutMapping(ApiPaths.ASSIGNED_TASKS_BY_ID)
    public ResponseEntity<ApiResponse<AssignedTaskResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAssignedTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(assignedTaskService.update(id, request)));
    }
}
