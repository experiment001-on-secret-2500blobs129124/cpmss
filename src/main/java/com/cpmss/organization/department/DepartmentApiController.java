package com.cpmss.organization.department;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.dto.CreateDepartmentRequest;
import com.cpmss.organization.department.dto.DepartmentResponse;
import com.cpmss.organization.department.dto.UpdateDepartmentRequest;
import com.cpmss.organization.departmentlocationhistory.dto.CreateDeptLocationHistoryRequest;
import com.cpmss.organization.departmentlocationhistory.dto.DeptLocationHistoryResponse;
import com.cpmss.organization.departmentmanagers.dto.CreateDeptManagerRequest;
import com.cpmss.organization.departmentmanagers.dto.DeptManagerResponse;
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

import java.util.List;
import java.util.UUID;

/**
 * REST controller for department CRUD and history sub-resources.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * delete, location-history, and manager assignment endpoints
 * under {@link ApiPaths#DEPARTMENTS}.
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

    // ── Location History Sub-Endpoints ─────────────────────────────────

    /**
     * Adds a location history entry to a department.
     *
     * @param id      the department UUID
     * @param request the location details (start date + building ID)
     * @return 201 Created with the new location history entry
     */
    @PostMapping(ApiPaths.DEPARTMENTS_LOCATION_HISTORY)
    public ResponseEntity<ApiResponse<DeptLocationHistoryResponse>> addLocationHistory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDeptLocationHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(departmentService.addLocationHistory(id, request)));
    }

    /**
     * Retrieves all location history entries for a department.
     *
     * @param id the department UUID
     * @return 200 OK with location history list
     */
    @GetMapping(ApiPaths.DEPARTMENTS_LOCATION_HISTORY)
    public ResponseEntity<ApiResponse<List<DeptLocationHistoryResponse>>> getLocationHistory(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.getLocationHistory(id)));
    }

    // ── Manager Sub-Endpoints ─────────────────────────────────────────

    /**
     * Assigns a manager to a department.
     *
     * @param id      the department UUID
     * @param request the manager assignment details
     * @return 201 Created with the new assignment
     */
    @PostMapping(ApiPaths.DEPARTMENTS_MANAGERS)
    public ResponseEntity<ApiResponse<DeptManagerResponse>> assignManager(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDeptManagerRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(departmentService.assignManager(id, request)));
    }


    /**
     * Retrieves the current manager assignment for a department.
     *
     * @param id the department UUID
     * @return 200 OK with current manager assignment
     */
    @GetMapping(ApiPaths.DEPARTMENTS_CURRENT_MANAGER)
    public ResponseEntity<ApiResponse<DeptManagerResponse>> getCurrentManager(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.getCurrentManager(id)));
    }

    /**
     * Retrieves all manager assignments for a department.
     *
     * @param id the department UUID
     * @return 200 OK with manager assignment list
     */
    @GetMapping(ApiPaths.DEPARTMENTS_MANAGERS)
    public ResponseEntity<ApiResponse<List<DeptManagerResponse>>> getManagers(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(departmentService.getManagers(id)));
    }
}
