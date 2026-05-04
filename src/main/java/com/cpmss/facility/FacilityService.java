package com.cpmss.facility;

import com.cpmss.building.Building;
import com.cpmss.building.BuildingRepository;
import com.cpmss.common.PagedResponse;
import com.cpmss.company.Company;
import com.cpmss.company.CompanyRepository;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.facility.dto.CreateFacilityRequest;
import com.cpmss.facility.dto.FacilityResponse;
import com.cpmss.facility.dto.UpdateFacilityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates facility lifecycle operations.
 *
 * <p>Facilities belong to a {@link Building} and may optionally be
 * managed by a {@link Company} (when management type is "Vendor").
 * Management type consistency is enforced by {@link FacilityRules}.
 *
 * @see FacilityRules
 * @see FacilityRepository
 */
@Service
public class FacilityService {

    private static final Logger log = LoggerFactory.getLogger(FacilityService.class);

    private final FacilityRepository repository;
    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;
    private final FacilityMapper mapper;
    private final FacilityRules rules = new FacilityRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         facility data access
     * @param buildingRepository building data access (for FK lookup)
     * @param companyRepository  company data access (for FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public FacilityService(FacilityRepository repository,
                           BuildingRepository buildingRepository,
                           CompanyRepository companyRepository,
                           FacilityMapper mapper) {
        this.repository = repository;
        this.buildingRepository = buildingRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a facility by its unique identifier.
     *
     * @param id the facility's UUID primary key
     * @return the matching facility response
     * @throws ResourceNotFoundException if no facility exists with this ID
     */
    @Transactional(readOnly = true)
    public FacilityResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id)));
    }

    /**
     * Lists all facilities with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of facility DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<FacilityResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new facility under the specified building.
     *
     * <p>Validates management type / company FK consistency before saving.
     *
     * @param request the create request with facility details
     * @return the created facility response
     * @throws ResourceNotFoundException if the building or company does not exist
     */
    @Transactional
    public FacilityResponse create(CreateFacilityRequest request) {
        rules.validateManagementType(request.managementType(), request.managedByCompanyId());

        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", request.buildingId()));

        Company company = resolveCompany(request.managedByCompanyId());

        Facility facility = Facility.builder()
                .facilityName(request.facilityName())
                .facilityCategory(request.facilityCategory())
                .managementType(request.managementType())
                .building(building)
                .managedByCompany(company)
                .build();
        facility = repository.save(facility);
        log.info("Facility created: {} in building {}", facility.getFacilityName(), building.getBuildingNo());
        return mapper.toResponse(facility);
    }

    /**
     * Updates an existing facility.
     *
     * @param id      the facility's UUID
     * @param request the update request with new values
     * @return the updated facility response
     * @throws ResourceNotFoundException if the facility, building, or company does not exist
     */
    @Transactional
    public FacilityResponse update(UUID id, UpdateFacilityRequest request) {
        Facility facility = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));

        rules.validateManagementType(request.managementType(), request.managedByCompanyId());

        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", request.buildingId()));

        Company company = resolveCompany(request.managedByCompanyId());

        facility.setFacilityName(request.facilityName());
        facility.setFacilityCategory(request.facilityCategory());
        facility.setManagementType(request.managementType());
        facility.setBuilding(building);
        facility.setManagedByCompany(company);
        facility = repository.save(facility);
        log.info("Facility updated: {}", facility.getFacilityName());
        return mapper.toResponse(facility);
    }

    /**
     * Deletes a facility by ID.
     *
     * @param id the facility's UUID
     * @throws ResourceNotFoundException if no facility exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Facility facility = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
        repository.delete(facility);
        log.info("Facility deleted: {}", facility.getFacilityName());
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Company resolveCompany(UUID companyId) {
        if (companyId == null) {
            return null;
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
    }
}
