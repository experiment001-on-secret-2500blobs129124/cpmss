package com.cpmss.security.accesspermit;

import com.cpmss.security.accesspermit.dto.AccessPermitResponse;
import com.cpmss.security.accesspermit.dto.CreateAccessPermitRequest;
import com.cpmss.security.accesspermit.dto.UpdateAccessPermitRequest;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for access permit CRUD operations.
 *
 * <p>Permits are revoked by status change, never deleted.
 *
 * @see AccessPermitService
 */
@RestController
public class AccessPermitApiController {

    private final AccessPermitService accessPermitService;

    /**
     * Constructs the controller with the access permit service.
     *
     * @param accessPermitService access permit orchestration service
     */
    public AccessPermitApiController(AccessPermitService accessPermitService) {
        this.accessPermitService = accessPermitService;
    }

    /**
     * Lists all access permits with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated permit list
     */
    @GetMapping(ApiPaths.ACCESS_PERMITS)
    public ResponseEntity<ApiResponse<PagedResponse<AccessPermitResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(accessPermitService.listAll(pageable)));
    }

    /**
     * Retrieves a single access permit by ID.
     *
     * @param id the permit UUID
     * @return 200 OK with the permit
     */
    @GetMapping(ApiPaths.ACCESS_PERMITS_BY_ID)
    public ResponseEntity<ApiResponse<AccessPermitResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(accessPermitService.getById(id)));
    }

    /**
     * Creates a new access permit.
     *
     * @param request the permit details and entitlement basis
     * @return 201 Created with the new permit
     */
    @PostMapping(ApiPaths.ACCESS_PERMITS)
    public ResponseEntity<ApiResponse<AccessPermitResponse>> create(
            @Valid @RequestBody CreateAccessPermitRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(accessPermitService.create(request)));
    }

    /**
     * Updates an existing access permit.
     *
     * @param id      the permit UUID
     * @param request the updated status / access level / expiry
     * @return 200 OK with the updated permit
     */
    @PutMapping(ApiPaths.ACCESS_PERMITS_BY_ID)
    public ResponseEntity<ApiResponse<AccessPermitResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccessPermitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(accessPermitService.update(id, request)));
    }
}
