package com.cpmss.internalreport;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.internalreport.dto.CreateInternalReportRequest;
import com.cpmss.internalreport.dto.InternalReportResponse;
import com.cpmss.internalreport.dto.UpdateInternalReportRequest;
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
 * REST controller for internal report operations.
 *
 * <p>Reports are never deleted — closed by status change.
 *
 * @see InternalReportService
 */
@RestController
public class InternalReportApiController {

    private final InternalReportService internalReportService;

    /**
     * Constructs the controller with the internal report service.
     *
     * @param internalReportService internal report orchestration service
     */
    public InternalReportApiController(InternalReportService internalReportService) {
        this.internalReportService = internalReportService;
    }

    /** Lists all reports with pagination. */
    @GetMapping(ApiPaths.INTERNAL_REPORTS)
    public ResponseEntity<ApiResponse<PagedResponse<InternalReportResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.listAll(pageable)));
    }

    /** Retrieves a single report by ID. */
    @GetMapping(ApiPaths.INTERNAL_REPORTS_BY_ID)
    public ResponseEntity<ApiResponse<InternalReportResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.getById(id)));
    }

    /** Files a new internal report. */
    @PostMapping(ApiPaths.INTERNAL_REPORTS)
    public ResponseEntity<ApiResponse<InternalReportResponse>> create(
            @Valid @RequestBody CreateInternalReportRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(internalReportService.create(request)));
    }

    /** Updates a report (status change, resolution). */
    @PutMapping(ApiPaths.INTERNAL_REPORTS_BY_ID)
    public ResponseEntity<ApiResponse<InternalReportResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInternalReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.update(id, request)));
    }
}
