package com.cpmss.security.gate;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.security.gate.dto.CreateGateRequest;
import com.cpmss.security.gate.dto.GateResponse;
import com.cpmss.security.gate.dto.UpdateGateRequest;
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
 * REST controller for gate CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#GATES}.
 *
 * @see GateService
 */
@RestController
public class GateApiController {

    private final GateService gateService;

    /**
     * Constructs the controller with the gate service.
     *
     * @param gateService gate orchestration service
     */
    public GateApiController(GateService gateService) {
        this.gateService = gateService;
    }

    /**
     * Lists all gates with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated gate list
     */
    @GetMapping(ApiPaths.GATES)
    public ResponseEntity<ApiResponse<PagedResponse<GateResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(gateService.listAll(pageable)));
    }

    /**
     * Retrieves a single gate by ID.
     *
     * @param id the gate UUID
     * @return 200 OK with the gate
     */
    @GetMapping(ApiPaths.GATES_BY_ID)
    public ResponseEntity<ApiResponse<GateResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(gateService.getById(id)));
    }

    /**
     * Creates a new gate.
     *
     * @param request the gate details and compound ID
     * @return 201 Created with the new gate
     */
    @PostMapping(ApiPaths.GATES)
    public ResponseEntity<ApiResponse<GateResponse>> create(
            @Valid @RequestBody CreateGateRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(gateService.create(request)));
    }

    /**
     * Updates an existing gate.
     *
     * @param id      the gate UUID
     * @param request the updated gate details
     * @return 200 OK with the updated gate
     */
    @PutMapping(ApiPaths.GATES_BY_ID)
    public ResponseEntity<ApiResponse<GateResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gateService.update(id, request)));
    }

    /**
     * Deletes a gate by ID.
     *
     * @param id the gate UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.GATES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        gateService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
