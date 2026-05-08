package com.cpmss.security.entersat;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.security.entersat.dto.CreateEntersAtRequest;
import com.cpmss.security.entersat.dto.EntersAtResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for gate access events.
 *
 * <p>Create-only — gate entries are immutable audit records.
 * No update or delete endpoints.
 *
 * @see EntersAtService
 */
@RestController
public class EntersAtApiController {

    private final EntersAtService entersAtService;

    /**
     * Constructs the controller with the gate entry service.
     *
     * @param entersAtService gate entry orchestration service
     */
    public EntersAtApiController(EntersAtService entersAtService) {
        this.entersAtService = entersAtService;
    }

    /**
     * Lists all gate entries with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated entry list
     */
    @GetMapping(ApiPaths.ENTRIES)
    public ResponseEntity<ApiResponse<PagedResponse<EntersAtResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(entersAtService.listAll(pageable)));
    }

    /**
     * Retrieves a single gate entry by ID.
     *
     * @param id the entry UUID
     * @return 200 OK with the entry
     */
    @GetMapping(ApiPaths.ENTRIES_BY_ID)
    public ResponseEntity<ApiResponse<EntersAtResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(entersAtService.getById(id)));
    }

    /**
     * Records a new gate access event.
     *
     * @param request the entry details
     * @return 201 Created with the new entry
     */
    @PostMapping(ApiPaths.ENTRIES)
    public ResponseEntity<ApiResponse<EntersAtResponse>> create(
            @Valid @RequestBody CreateEntersAtRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(entersAtService.create(request)));
    }
}
