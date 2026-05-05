package com.cpmss.unit;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.unit.dto.CreateUnitRequest;
import com.cpmss.unit.dto.UnitResponse;
import com.cpmss.unit.dto.UpdateUnitRequest;
import com.cpmss.unitpricinghistory.dto.CreateUnitPricingHistoryRequest;
import com.cpmss.unitpricinghistory.dto.UnitPricingHistoryResponse;
import com.cpmss.unitstatushistory.dto.CreateUnitStatusHistoryRequest;
import com.cpmss.unitstatushistory.dto.UnitStatusHistoryResponse;
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
 * REST controller for unit CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#UNITS}.
 *
 * @see UnitService
 */
@RestController
public class UnitApiController {

    private final UnitService unitService;

    /**
     * Constructs the controller with the unit service.
     *
     * @param unitService unit orchestration service
     */
    public UnitApiController(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * Lists all units with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated unit list
     */
    @GetMapping(ApiPaths.UNITS)
    public ResponseEntity<ApiResponse<PagedResponse<UnitResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.listAll(pageable)));
    }

    /**
     * Retrieves a single unit by ID.
     *
     * @param id the unit UUID
     * @return 200 OK with the unit
     */
    @GetMapping(ApiPaths.UNITS_BY_ID)
    public ResponseEntity<ApiResponse<UnitResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getById(id)));
    }

    /**
     * Creates a new unit.
     *
     * @param request the unit details and building ID
     * @return 201 Created with the new unit
     */
    @PostMapping(ApiPaths.UNITS)
    public ResponseEntity<ApiResponse<UnitResponse>> create(
            @Valid @RequestBody CreateUnitRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(unitService.create(request)));
    }

    /**
     * Updates an existing unit.
     *
     * @param id      the unit UUID
     * @param request the updated unit details
     * @return 200 OK with the updated unit
     */
    @PutMapping(ApiPaths.UNITS_BY_ID)
    public ResponseEntity<ApiResponse<UnitResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUnitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.update(id, request)));
    }

    /**
     * Deletes a unit by ID.
     *
     * @param id the unit UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.UNITS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        unitService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }

    // ── Pricing History Sub-Endpoints ────────────────────────────────────

    /**
     * Adds a pricing history entry to a unit.
     *
     * @param id      the unit UUID
     * @param request the pricing details
     * @return 201 Created with the new pricing history entry
     */
    @PostMapping(ApiPaths.UNITS_PRICING_HISTORY)
    public ResponseEntity<ApiResponse<UnitPricingHistoryResponse>> addPricingHistory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUnitPricingHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(unitService.addPricingHistory(id, request)));
    }

    /**
     * Retrieves all pricing history entries for a unit.
     *
     * @param id the unit UUID
     * @return 200 OK with pricing history list
     */
    @GetMapping(ApiPaths.UNITS_PRICING_HISTORY)
    public ResponseEntity<ApiResponse<List<UnitPricingHistoryResponse>>> getPricingHistory(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getPricingHistory(id)));
    }

    // ── Status History Sub-Endpoints ─────────────────────────────────────

    /**
     * Adds a status history entry to a unit.
     *
     * @param id      the unit UUID
     * @param request the status details
     * @return 201 Created with the new status history entry
     */
    @PostMapping(ApiPaths.UNITS_STATUS_HISTORY)
    public ResponseEntity<ApiResponse<UnitStatusHistoryResponse>> addStatusHistory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUnitStatusHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(unitService.addStatusHistory(id, request)));
    }

    /**
     * Retrieves all status history entries for a unit.
     *
     * @param id the unit UUID
     * @return 200 OK with status history list
     */
    @GetMapping(ApiPaths.UNITS_STATUS_HISTORY)
    public ResponseEntity<ApiResponse<List<UnitStatusHistoryResponse>>> getStatusHistory(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getStatusHistory(id)));
    }
}
