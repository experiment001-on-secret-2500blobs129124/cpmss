package com.cpmss.maintenance.company;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.maintenance.company.dto.CompanyResponse;
import com.cpmss.maintenance.company.dto.CreateCompanyRequest;
import com.cpmss.maintenance.company.dto.UpdateCompanyRequest;
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
 * REST controller for company CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#COMPANIES}.
 *
 * @see CompanyService
 */
@RestController
public class CompanyApiController {

    private final CompanyService companyService;

    /**
     * Constructs the controller with the company service.
     *
     * @param companyService company orchestration service
     */
    public CompanyApiController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Lists all companies with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated company list
     */
    @GetMapping(ApiPaths.COMPANIES)
    public ResponseEntity<ApiResponse<PagedResponse<CompanyResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.listAll(pageable)));
    }

    /**
     * Retrieves a single company by ID.
     *
     * @param id the company UUID
     * @return 200 OK with the company
     */
    @GetMapping(ApiPaths.COMPANIES_BY_ID)
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getById(id)));
    }

    /**
     * Creates a new company.
     *
     * @param request the company details
     * @return 201 Created with the new company
     */
    @PostMapping(ApiPaths.COMPANIES)
    public ResponseEntity<ApiResponse<CompanyResponse>> create(
            @Valid @RequestBody CreateCompanyRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(companyService.create(request)));
    }

    /**
     * Updates an existing company.
     *
     * @param id      the company UUID
     * @param request the updated company details
     * @return 200 OK with the updated company
     */
    @PutMapping(ApiPaths.COMPANIES_BY_ID)
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.update(id, request)));
    }

    /**
     * Deletes a company by ID.
     *
     * @param id the company UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.COMPANIES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        companyService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
