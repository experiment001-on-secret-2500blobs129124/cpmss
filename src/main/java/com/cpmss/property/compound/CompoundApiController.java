package com.cpmss.property.compound;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.property.compound.dto.CompoundResponse;
import com.cpmss.property.compound.dto.CreateCompoundRequest;
import com.cpmss.property.compound.dto.UpdateCompoundRequest;
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
 * REST controller for compound CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#COMPOUNDS}.
 *
 * @see CompoundService
 */
@RestController
public class CompoundApiController {

    private final CompoundService compoundService;

    /**
     * Constructs the controller with the compound service.
     *
     * @param compoundService compound orchestration service
     */
    public CompoundApiController(CompoundService compoundService) {
        this.compoundService = compoundService;
    }

    /**
     * Lists all compounds with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated compound list
     */
    @GetMapping(ApiPaths.COMPOUNDS)
    public ResponseEntity<ApiResponse<PagedResponse<CompoundResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(compoundService.listAll(pageable)));
    }

    /**
     * Retrieves a single compound by ID.
     *
     * @param id the compound UUID
     * @return 200 OK with the compound
     */
    @GetMapping(ApiPaths.COMPOUNDS_BY_ID)
    public ResponseEntity<ApiResponse<CompoundResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(compoundService.getById(id)));
    }

    /**
     * Creates a new compound.
     *
     * @param request the compound details
     * @return 201 Created with the new compound
     */
    @PostMapping(ApiPaths.COMPOUNDS)
    public ResponseEntity<ApiResponse<CompoundResponse>> create(
            @Valid @RequestBody CreateCompoundRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(compoundService.create(request)));
    }

    /**
     * Updates an existing compound.
     *
     * @param id      the compound UUID
     * @param request the updated compound details
     * @return 200 OK with the updated compound
     */
    @PutMapping(ApiPaths.COMPOUNDS_BY_ID)
    public ResponseEntity<ApiResponse<CompoundResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompoundRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(compoundService.update(id, request)));
    }

    /**
     * Deletes a compound by ID.
     *
     * @param id the compound UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.COMPOUNDS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        compoundService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
