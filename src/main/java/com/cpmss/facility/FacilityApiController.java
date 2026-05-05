package com.cpmss.facility;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.facility.dto.CreateFacilityRequest;
import com.cpmss.facility.dto.FacilityResponse;
import com.cpmss.facility.dto.UpdateFacilityRequest;
import com.cpmss.facilityhourshistory.dto.CreateFacilityHoursHistoryRequest;
import com.cpmss.facilityhourshistory.dto.FacilityHoursHistoryResponse;
import com.cpmss.facilitymanager.dto.CreateFacilityManagerRequest;
import com.cpmss.facilitymanager.dto.FacilityManagerResponse;
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
 * REST controller for facility CRUD and history sub-resources.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * delete, hours-history, and manager assignment endpoints
 * under {@link ApiPaths#FACILITIES}.
 *
 * @see FacilityService
 */
@RestController
public class FacilityApiController {

    private final FacilityService facilityService;

    /**
     * Constructs the controller with the facility service.
     *
     * @param facilityService facility orchestration service
     */
    public FacilityApiController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    /**
     * Lists all facilities with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated facility list
     */
    @GetMapping(ApiPaths.FACILITIES)
    public ResponseEntity<ApiResponse<PagedResponse<FacilityResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(facilityService.listAll(pageable)));
    }

    /**
     * Retrieves a single facility by ID.
     *
     * @param id the facility UUID
     * @return 200 OK with the facility
     */
    @GetMapping(ApiPaths.FACILITIES_BY_ID)
    public ResponseEntity<ApiResponse<FacilityResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(facilityService.getById(id)));
    }

    /**
     * Creates a new facility.
     *
     * @param request the facility details
     * @return 201 Created with the new facility
     */
    @PostMapping(ApiPaths.FACILITIES)
    public ResponseEntity<ApiResponse<FacilityResponse>> create(
            @Valid @RequestBody CreateFacilityRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(facilityService.create(request)));
    }

    /**
     * Updates an existing facility.
     *
     * @param id      the facility UUID
     * @param request the updated facility details
     * @return 200 OK with the updated facility
     */
    @PutMapping(ApiPaths.FACILITIES_BY_ID)
    public ResponseEntity<ApiResponse<FacilityResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFacilityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(facilityService.update(id, request)));
    }

    /**
     * Deletes a facility by ID.
     *
     * @param id the facility UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.FACILITIES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        facilityService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }

    // ── Hours History Sub-Endpoints ────────────────────────────────────

    /**
     * Adds a hours history entry to a facility.
     *
     * @param id      the facility UUID
     * @param request the hours details
     * @return 201 Created with the new hours history entry
     */
    @PostMapping(ApiPaths.FACILITIES_HOURS_HISTORY)
    public ResponseEntity<ApiResponse<FacilityHoursHistoryResponse>> addHoursHistory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateFacilityHoursHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(facilityService.addHoursHistory(id, request)));
    }

    /**
     * Retrieves all hours history entries for a facility.
     *
     * @param id the facility UUID
     * @return 200 OK with hours history list
     */
    @GetMapping(ApiPaths.FACILITIES_HOURS_HISTORY)
    public ResponseEntity<ApiResponse<List<FacilityHoursHistoryResponse>>> getHoursHistory(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(facilityService.getHoursHistory(id)));
    }

    // ── Manager Sub-Endpoints ─────────────────────────────────────────

    /**
     * Assigns a manager to a facility.
     *
     * @param id      the facility UUID
     * @param request the manager assignment details
     * @return 201 Created with the new assignment
     */
    @PostMapping(ApiPaths.FACILITIES_MANAGERS)
    public ResponseEntity<ApiResponse<FacilityManagerResponse>> assignManager(
            @PathVariable UUID id,
            @Valid @RequestBody CreateFacilityManagerRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(facilityService.assignManager(id, request)));
    }

    /**
     * Retrieves all manager assignments for a facility.
     *
     * @param id the facility UUID
     * @return 200 OK with manager assignment list
     */
    @GetMapping(ApiPaths.FACILITIES_MANAGERS)
    public ResponseEntity<ApiResponse<List<FacilityManagerResponse>>> getManagers(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(facilityService.getManagers(id)));
    }
}
