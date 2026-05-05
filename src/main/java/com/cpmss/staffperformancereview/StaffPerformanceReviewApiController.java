package com.cpmss.staffperformancereview;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.staffperformancereview.dto.CreateStaffPerformanceReviewRequest;
import com.cpmss.staffperformancereview.dto.StaffPerformanceReviewResponse;
import com.cpmss.staffperformancereview.dto.UpdateStaffPerformanceReviewRequest;
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

/** REST controller for performance review CRUD operations. */
@RestController
public class StaffPerformanceReviewApiController {

    private final StaffPerformanceReviewService service;

    public StaffPerformanceReviewApiController(StaffPerformanceReviewService service) {
        this.service = service;
    }

    @GetMapping(ApiPaths.PERFORMANCE_REVIEWS)
    public ResponseEntity<ApiResponse<PagedResponse<StaffPerformanceReviewResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.listAll(pageable)));
    }

    @GetMapping(ApiPaths.PERFORMANCE_REVIEWS_BY_ID)
    public ResponseEntity<ApiResponse<StaffPerformanceReviewResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping(ApiPaths.PERFORMANCE_REVIEWS)
    public ResponseEntity<ApiResponse<StaffPerformanceReviewResponse>> create(
            @Valid @RequestBody CreateStaffPerformanceReviewRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.create(request)));
    }

    @PutMapping(ApiPaths.PERFORMANCE_REVIEWS_BY_ID)
    public ResponseEntity<ApiResponse<StaffPerformanceReviewResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateStaffPerformanceReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }
}
