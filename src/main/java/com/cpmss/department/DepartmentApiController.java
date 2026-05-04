package com.cpmss.department;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.department.dto.CreateDepartmentRequest;
import com.cpmss.department.dto.DepartmentResponse;
import com.cpmss.department.dto.UpdateDepartmentRequest;
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
 * REST controller for department CRUD operations.
 *
 * @see DepartmentService
 */
@RestController
public class DepartmentApiController {

    private final DepartmentService departmentService;

    /**
     * Constructs the controller with the department service.
     *
     * @param departmentService department orchestration service
     */
    public DepartmentApiController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * Lists all departments with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated department list
     */
    @GetMapping(ApiPaths.DEPARTMENTS)
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.listAll(pageable)));
    }

    /**
     * Retrieves a single department by ID.
     *
     * @param id the department UUID
     * @return 200 OK with the department
     */
    @GetMapping(ApiPaths.DEPARTMENTS_BY_ID)
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.getById(id)));
    }

    /**
     * Creates a new department.
     *
     * @param request the department name
     * @return 201 Created with the new department
     */
    @PostMapping(ApiPaths.DEPARTMENTS)
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(
            @Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse response = departmentService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    /**
     * Updates an existing department.
     *
     * @param id      the department UUID
     * @param request the updated department name
     * @return 200 OK with the updated department
     */
    @PutMapping(ApiPaths.DEPARTMENTS_BY_ID)
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.update(id, request)));
    }

    /**
     * Deletes a department by ID.
     *
     * @param id the department UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.DEPARTMENTS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        departmentService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
