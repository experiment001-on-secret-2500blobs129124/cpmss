package com.cpmss.hr.staffposition;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.hr.staffposition.dto.CreatePositionSalaryHistoryRequest;
import com.cpmss.hr.staffposition.dto.CreateStaffPositionRequest;
import com.cpmss.hr.staffposition.dto.PositionSalaryHistoryResponse;
import com.cpmss.hr.staffposition.dto.StaffPositionResponse;
import com.cpmss.hr.staffposition.dto.UpdateStaffPositionRequest;
import com.cpmss.hr.staffpositionhistory.dto.CreateStaffPositionHistoryRequest;
import com.cpmss.hr.staffpositionhistory.dto.StaffPositionHistoryResponse;
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

    /**
     * Assigns a staff member to a position and closes the previous active row.
     *
     * @param request assignment details
     * @return 201 Created with the new history row
     */
    @PostMapping(ApiPaths.STAFF_POSITION_HISTORY)
    public ResponseEntity<ApiResponse<StaffPositionHistoryResponse>> assignStaffPosition(
            @Valid @RequestBody CreateStaffPositionHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(service.assignStaffPosition(request)));
    }

    /**
     * Lists position assignment history for one person.
     *
     * @param personId the staff member UUID
     * @return 200 OK with assignment history
     */
    @GetMapping(ApiPaths.STAFF_POSITION_HISTORY_BY_PERSON)
    public ResponseEntity<ApiResponse<List<StaffPositionHistoryResponse>>> positionHistoryForPerson(
            @PathVariable UUID personId) {
        return ResponseEntity.ok(ApiResponse.ok(service.positionHistoryForPerson(personId)));
    }

    /**
     * Records a salary band for a staff position.
     *
     * @param id the position UUID
     * @param request salary-band details
     * @return 201 Created with the salary history row
     */
    @PostMapping(ApiPaths.STAFF_POSITIONS_SALARY_HISTORY)
    public ResponseEntity<ApiResponse<PositionSalaryHistoryResponse>> createPositionSalaryHistory(
            @PathVariable UUID id,
            @Valid @RequestBody CreatePositionSalaryHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(service.createPositionSalaryHistory(id, request)));
    }

    /**
     * Lists salary-band history for a staff position.
     *
     * @param id the position UUID
     * @return 200 OK with salary-band history
     */
    @GetMapping(ApiPaths.STAFF_POSITIONS_SALARY_HISTORY)
    public ResponseEntity<ApiResponse<List<PositionSalaryHistoryResponse>>> positionSalaryHistory(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.positionSalaryHistory(id)));
    }
}
