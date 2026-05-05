package com.cpmss.kpipolicy;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.kpipolicy.dto.CreateKpiPolicyRequest;
import com.cpmss.kpipolicy.dto.KpiPolicyResponse;
import com.cpmss.kpipolicy.dto.UpdateKpiPolicyRequest;
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

/** REST controller for KPI policy tier CRUD operations. */
@RestController
public class KpiPolicyApiController {

    private final KpiPolicyService kpiPolicyService;

    public KpiPolicyApiController(KpiPolicyService kpiPolicyService) {
        this.kpiPolicyService = kpiPolicyService;
    }

    @GetMapping(ApiPaths.KPI_POLICIES)
    public ResponseEntity<ApiResponse<PagedResponse<KpiPolicyResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(kpiPolicyService.listAll(pageable)));
    }

    @GetMapping(ApiPaths.KPI_POLICIES_BY_ID)
    public ResponseEntity<ApiResponse<KpiPolicyResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(kpiPolicyService.getById(id)));
    }

    @PostMapping(ApiPaths.KPI_POLICIES)
    public ResponseEntity<ApiResponse<KpiPolicyResponse>> create(
            @Valid @RequestBody CreateKpiPolicyRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(kpiPolicyService.create(request)));
    }

    @PutMapping(ApiPaths.KPI_POLICIES_BY_ID)
    public ResponseEntity<ApiResponse<KpiPolicyResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateKpiPolicyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(kpiPolicyService.update(id, request)));
    }
}
