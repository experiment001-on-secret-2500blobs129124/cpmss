package com.cpmss.property.building;

import com.cpmss.property.building.dto.BuildingResponse;
import com.cpmss.property.building.dto.CreateBuildingRequest;
import com.cpmss.property.building.dto.UpdateBuildingRequest;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
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
 * REST controller for building CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#BUILDINGS}.
 *
 * @see BuildingService
 */
@RestController
public class BuildingApiController {

    private final BuildingService buildingService;

    /**
     * Constructs the controller with the building service.
     *
     * @param buildingService building orchestration service
     */
    public BuildingApiController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    /**
     * Lists all buildings with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated building list
     */
    @GetMapping(ApiPaths.BUILDINGS)
    public ResponseEntity<ApiResponse<PagedResponse<BuildingResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(buildingService.listAll(pageable)));
    }

    /**
     * Retrieves a single building by ID.
     *
     * @param id the building UUID
     * @return 200 OK with the building
     */
    @GetMapping(ApiPaths.BUILDINGS_BY_ID)
    public ResponseEntity<ApiResponse<BuildingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(buildingService.getById(id)));
    }

    /**
     * Creates a new building.
     *
     * @param request the building details and compound ID
     * @return 201 Created with the new building
     */
    @PostMapping(ApiPaths.BUILDINGS)
    public ResponseEntity<ApiResponse<BuildingResponse>> create(
            @Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(buildingService.create(request)));
    }

    /**
     * Updates an existing building.
     *
     * @param id      the building UUID
     * @param request the updated building details
     * @return 200 OK with the updated building
     */
    @PutMapping(ApiPaths.BUILDINGS_BY_ID)
    public ResponseEntity<ApiResponse<BuildingResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBuildingRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(buildingService.update(id, request)));
    }

    /**
     * Deletes a building by ID.
     *
     * @param id the building UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.BUILDINGS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        buildingService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
