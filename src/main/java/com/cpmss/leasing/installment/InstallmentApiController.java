package com.cpmss.leasing.installment;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.installment.dto.CreateInstallmentRequest;
import com.cpmss.leasing.installment.dto.InstallmentResponse;
import com.cpmss.leasing.installment.dto.UpdateInstallmentRequest;
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
 * REST controller for installment CRUD operations.
 *
 * <p>No delete endpoint — installments are permanent financial records.
 *
 * @see InstallmentService
 */
@RestController
public class InstallmentApiController {

    private final InstallmentService installmentService;

    /**
     * Constructs the controller with the installment service.
     *
     * @param installmentService installment orchestration service
     */
    public InstallmentApiController(InstallmentService installmentService) {
        this.installmentService = installmentService;
    }

    /**
     * Lists all installments with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated installment list
     */
    @GetMapping(ApiPaths.INSTALLMENTS)
    public ResponseEntity<ApiResponse<PagedResponse<InstallmentResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(installmentService.listAll(pageable)));
    }

    /**
     * Retrieves a single installment by ID.
     *
     * @param id the installment UUID
     * @return 200 OK with the installment
     */
    @GetMapping(ApiPaths.INSTALLMENTS_BY_ID)
    public ResponseEntity<ApiResponse<InstallmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(installmentService.getById(id)));
    }

    /**
     * Creates a new installment.
     *
     * @param request the installment details and contract ID
     * @return 201 Created with the new installment
     */
    @PostMapping(ApiPaths.INSTALLMENTS)
    public ResponseEntity<ApiResponse<InstallmentResponse>> create(
            @Valid @RequestBody CreateInstallmentRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(installmentService.create(request)));
    }

    /**
     * Updates an existing installment.
     *
     * @param id      the installment UUID
     * @param request the updated installment details
     * @return 200 OK with the updated installment
     */
    @PutMapping(ApiPaths.INSTALLMENTS_BY_ID)
    public ResponseEntity<ApiResponse<InstallmentResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInstallmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(installmentService.update(id, request)));
    }
}
