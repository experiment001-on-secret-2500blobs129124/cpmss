package com.cpmss.communication.internalreport;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.communication.internalreport.dto.CreateInternalReportRequest;
import com.cpmss.communication.internalreport.dto.InternalReportResponse;
import com.cpmss.communication.internalreport.dto.MarkReportReadRequest;
import com.cpmss.communication.internalreport.dto.ResolveInternalReportRequest;
import com.cpmss.communication.internalreport.dto.UpdateInternalReportRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for internal report operations.
 *
 * <p>Reports are never deleted — closed by status change.
 * Supports pool model (list by role) and notification badges.
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

    /**
     * Lists reports — filtered by role (pool) or by reporter if query
     * params provided, otherwise returns all with pagination.
     *
     * @param role       filter by assigned role (optional)
     * @param reporterId filter by reporter person UUID (optional)
     * @param pageable   pagination parameters (used when no filters)
     * @return 200 OK with report list
     */
    @GetMapping(ApiPaths.INTERNAL_REPORTS)
    public ResponseEntity<ApiResponse<?>> listAll(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) UUID reporterId,
            Pageable pageable) {
        if (role != null) {
            List<InternalReportResponse> reports = internalReportService.listByRole(role);
            return ResponseEntity.ok(ApiResponse.ok(reports));
        }
        if (reporterId != null) {
            List<InternalReportResponse> reports = internalReportService.listByReporter(reporterId);
            return ResponseEntity.ok(ApiResponse.ok(reports));
        }
        PagedResponse<InternalReportResponse> paged = internalReportService.listAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok(paged));
    }

    /**
     * Returns the unread report count for a role (notification badge).
     *
     * @param role the assigned system role
     * @return 200 OK with unread count
     */
    @GetMapping(ApiPaths.INTERNAL_REPORTS_UNREAD_COUNT)
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@RequestParam String role) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.countUnreadByRole(role)));
    }

    /**
     * Retrieves a single report by ID.
     *
     * @param id the report UUID
     * @return 200 OK with the report
     */
    @GetMapping(ApiPaths.INTERNAL_REPORTS_BY_ID)
    public ResponseEntity<ApiResponse<InternalReportResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.getById(id)));
    }

    /**
     * Files a new internal report.
     *
     * @param request the report details
     * @return 201 Created with the new report
     */
    @PostMapping(ApiPaths.INTERNAL_REPORTS)
    public ResponseEntity<ApiResponse<InternalReportResponse>> create(
            @Valid @RequestBody CreateInternalReportRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(internalReportService.create(request)));
    }

    /**
     * Updates a report (status change, resolution).
     *
     * @param id      the report UUID
     * @param request the updated status / resolution
     * @return 200 OK with the updated report
     */
    @PutMapping(ApiPaths.INTERNAL_REPORTS_BY_ID)
    public ResponseEntity<ApiResponse<InternalReportResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInternalReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.update(id, request)));
    }

    /**
     * Marks a report as read.
     *
     * @param id      the report UUID
     * @param request the reader details
     * @return 200 OK with the updated report
     */
    @PutMapping(ApiPaths.INTERNAL_REPORTS_READ)
    public ResponseEntity<ApiResponse<InternalReportResponse>> markAsRead(
            @PathVariable UUID id,
            @Valid @RequestBody MarkReportReadRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                internalReportService.markAsRead(id, request.readById())));
    }

    /**
     * Marks a report as unread.
     *
     * @param id the report UUID
     * @return 200 OK with the updated report
     */
    @PutMapping(ApiPaths.INTERNAL_REPORTS_UNREAD)
    public ResponseEntity<ApiResponse<InternalReportResponse>> markAsUnread(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(internalReportService.markAsUnread(id)));
    }

    /**
     * Resolves a report with a resolution note.
     *
     * @param id      the report UUID
     * @param request the resolution details
     * @return 200 OK with the resolved report
     */
    @PutMapping(ApiPaths.INTERNAL_REPORTS_RESOLVE)
    public ResponseEntity<ApiResponse<InternalReportResponse>> resolve(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveInternalReportRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                internalReportService.resolve(id, request.resolvedById(),
                        request.resolutionNote())));
    }
}
