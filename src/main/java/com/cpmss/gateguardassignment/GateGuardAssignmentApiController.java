package com.cpmss.gateguardassignment;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.gateguardassignment.dto.CreateGateGuardAssignmentRequest;
import com.cpmss.gateguardassignment.dto.GateGuardAssignmentResponse;
import com.cpmss.gateguardassignment.dto.UpdateGateGuardAssignmentRequest;
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

/** REST controller for gate guard assignment operations. */
@RestController
public class GateGuardAssignmentApiController {

    private final GateGuardAssignmentService service;

    public GateGuardAssignmentApiController(GateGuardAssignmentService service) {
        this.service = service;
    }

    @GetMapping(ApiPaths.GATE_GUARD_ASSIGNMENTS)
    public ResponseEntity<ApiResponse<PagedResponse<GateGuardAssignmentResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.listAll(pageable)));
    }

    @GetMapping(ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID)
    public ResponseEntity<ApiResponse<GateGuardAssignmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping(ApiPaths.GATE_GUARD_ASSIGNMENTS)
    public ResponseEntity<ApiResponse<GateGuardAssignmentResponse>> create(
            @Valid @RequestBody CreateGateGuardAssignmentRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.create(request)));
    }

    @PutMapping(ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID)
    public ResponseEntity<ApiResponse<GateGuardAssignmentResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateGateGuardAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }
}
