package com.cpmss.security.vehicle;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.security.vehicle.dto.CreateVehicleRequest;
import com.cpmss.security.vehicle.dto.UpdateVehicleRequest;
import com.cpmss.security.vehicle.dto.VehiclePermitLinkResponse;
import com.cpmss.security.vehicle.dto.VehicleResponse;
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
 * REST controller for vehicle CRUD and permit-link operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * delete, and parent-managed vehicle-permit link workflows under
 * {@link ApiPaths#VEHICLES}.
 *
 * @see VehicleService
 */
@RestController
public class VehicleApiController {

    private final VehicleService vehicleService;

    /**
     * Constructs the controller with the vehicle service.
     *
     * @param vehicleService vehicle orchestration service
     */
    public VehicleApiController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Lists all vehicles with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated vehicle list
     */
    @GetMapping(ApiPaths.VEHICLES)
    public ResponseEntity<ApiResponse<PagedResponse<VehicleResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.listAll(pageable)));
    }

    /**
     * Retrieves a single vehicle by ID.
     *
     * @param id the vehicle UUID
     * @return 200 OK with the vehicle
     */
    @GetMapping(ApiPaths.VEHICLES_BY_ID)
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getById(id)));
    }

    /**
     * Creates a new vehicle.
     *
     * @param request the vehicle details and owner ID
     * @return 201 Created with the new vehicle
     */
    @PostMapping(ApiPaths.VEHICLES)
    public ResponseEntity<ApiResponse<VehicleResponse>> create(
            @Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(vehicleService.create(request)));
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id      the vehicle UUID
     * @param request the updated vehicle details
     * @return 200 OK with the updated vehicle
     */
    @PutMapping(ApiPaths.VEHICLES_BY_ID)
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.update(id, request)));
    }

    /**
     * Links an access permit to a vehicle.
     *
     * @param id the vehicle UUID
     * @param permitId the access permit UUID
     * @return 201 Created with the vehicle-permit link
     */
    @PostMapping(ApiPaths.VEHICLE_PERMIT)
    public ResponseEntity<ApiResponse<VehiclePermitLinkResponse>> linkPermit(
            @PathVariable UUID id,
            @PathVariable UUID permitId) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(vehicleService.linkPermit(id, permitId)));
    }

    /**
     * Unlinks an access permit from a vehicle.
     *
     * @param id the vehicle UUID
     * @param permitId the access permit UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.VEHICLE_PERMIT)
    public ResponseEntity<ApiResponse<Void>> unlinkPermit(
            @PathVariable UUID id,
            @PathVariable UUID permitId) {
        vehicleService.unlinkPermit(id, permitId);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param id the vehicle UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.VEHICLES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
