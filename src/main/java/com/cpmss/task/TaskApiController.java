package com.cpmss.task;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.task.dto.CreateTaskRequest;
import com.cpmss.task.dto.TaskResponse;
import com.cpmss.task.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for task CRUD operations.
 *
 * @see TaskService
 */
@RestController
public class TaskApiController {

    private final TaskService taskService;

    /**
     * Constructs the controller with the task service.
     *
     * @param taskService task orchestration service
     */
    public TaskApiController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Lists all tasks with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated task list
     */
    @GetMapping(ApiPaths.TASKS)
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(taskService.listAll(pageable)));
    }

    /**
     * Retrieves a single task by ID.
     *
     * @param id the task UUID
     * @return 200 OK with the task
     */
    @GetMapping(ApiPaths.TASKS_BY_ID)
    public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(taskService.getById(id)));
    }

    /**
     * Creates a new task.
     *
     * @param request the task title and department ID
     * @return 201 Created with the new task
     */
    @PostMapping(ApiPaths.TASKS)
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(taskService.create(request)));
    }

    /**
     * Updates an existing task.
     *
     * @param id      the task UUID
     * @param request the updated title and department ID
     * @return 200 OK with the updated task
     */
    @PutMapping(ApiPaths.TASKS_BY_ID)
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(taskService.update(id, request)));
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the task UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.TASKS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
