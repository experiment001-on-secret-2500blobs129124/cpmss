package com.cpmss.staffposition;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.staffposition.dto.CreateStaffPositionRequest;
import com.cpmss.staffposition.dto.StaffPositionResponse;
import com.cpmss.staffposition.dto.UpdateStaffPositionRequest;
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
 * REST controller for staff position CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#STAFF_POSITIONS}.
 *
 * @see StaffPositionService
 */
@RestController
public class StaffPositionApiController {

    private final StaffPositionService service;

    /**
     * Constructs the controller with the staff position service.
     *
     * @param service staff position orchestration service
     */
    public StaffPositionApiController(StaffPositionService service) {
        this.service = service;
    }

    /**
     * Lists all staff positions with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated list
     */
    @GetMapping(ApiPaths.STAFF_POSITIONS)
    public ResponseEntity<ApiResponse<PagedResponse<StaffPositionResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.listAll(pageable)));
    }

    /**
     * Retrieves a single staff position by ID.
     *
     * @param id the position UUID
     * @return 200 OK with the position
     */
    @GetMapping(ApiPaths.STAFF_POSITIONS_BY_ID)
    public ResponseEntity<ApiResponse<StaffPositionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    /**
     * Creates a new staff position.
     *
     * @param request the position details
     * @return 201 Created with the new position
     */
    @PostMapping(ApiPaths.STAFF_POSITIONS)
    public ResponseEntity<ApiResponse<StaffPositionResponse>> create(
            @Valid @RequestBody CreateStaffPositionRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(service.create(request)));
    }

    /**
     * Updates an existing staff position.
     *
     * @param id      the position UUID
     * @param request the updated details
     * @return 200 OK with the updated position
     */
    @PutMapping(ApiPaths.STAFF_POSITIONS_BY_ID)
    public ResponseEntity<ApiResponse<StaffPositionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffPositionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    /**
     * Deletes a staff position by ID.
     *
     * @param id the position UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.STAFF_POSITIONS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
