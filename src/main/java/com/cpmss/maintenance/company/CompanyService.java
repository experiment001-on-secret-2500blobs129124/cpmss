package com.cpmss.maintenance.company;

import com.cpmss.maintenance.common.MaintenanceErrorCode;
import com.cpmss.maintenance.company.dto.CompanyResponse;
import com.cpmss.maintenance.company.dto.CreateCompanyRequest;
import com.cpmss.maintenance.company.dto.UpdateCompanyRequest;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates company lifecycle operations.
 *
 * <p>Handles CRUD for {@link Company} entities. Companies represent
 * external organizations associated with the compound — vendors,
 * contractors, and service providers.
 *
 * @see CompanyRepository
 */
@Service
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository repository;
    private final CompanyMapper mapper;

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository company data access
     * @param mapper     entity-DTO mapper
     */
    public CompanyService(CompanyRepository repository, CompanyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param id the company's UUID primary key
     * @return the matching company response
     * @throws ApiException if no company exists with this ID
     */
    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND)));
    }

    /**
     * Lists all companies with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of company DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<CompanyResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new company.
     *
     * @param request the create request with company details
     * @return the created company response
     */
    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {
        Company company = mapper.toEntity(request);
        company = repository.save(company);
        log.info("Company created: {}", company.getCompanyName());
        return mapper.toResponse(company);
    }

    /**
     * Updates an existing company.
     *
     * @param id      the company's UUID
     * @param request the update request with new values
     * @return the updated company response
     * @throws ApiException if no company exists with this ID
     */
    @Transactional
    public CompanyResponse update(UUID id, UpdateCompanyRequest request) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND));
        company.setCompanyName(request.companyName());
        company.setTaxId(request.taxId());
        company.setPhoneNo(request.phoneNo());
        company.setCompanyType(request.companyType());
        company = repository.save(company);
        log.info("Company updated: {}", company.getCompanyName());
        return mapper.toResponse(company);
    }

    /**
     * Deletes a company by ID.
     *
     * @param id the company's UUID
     * @throws ApiException if no company exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ApiException(MaintenanceErrorCode.COMPANY_NOT_FOUND));
        repository.delete(company);
        log.info("Company deleted: {}", company.getCompanyName());
    }
}
