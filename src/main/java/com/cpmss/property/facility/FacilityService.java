package com.cpmss.property.facility;

import com.cpmss.property.building.Building;
import com.cpmss.property.building.BuildingRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.maintenance.company.Company;
import com.cpmss.maintenance.company.CompanyRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.property.facility.dto.CreateFacilityRequest;
import com.cpmss.property.facility.dto.FacilityResponse;
import com.cpmss.property.facility.dto.UpdateFacilityRequest;
import com.cpmss.property.facilityhourshistory.FacilityHoursHistory;
import com.cpmss.property.facilityhourshistory.FacilityHoursHistoryRepository;
import com.cpmss.property.facilityhourshistory.dto.CreateFacilityHoursHistoryRequest;
import com.cpmss.property.facilityhourshistory.dto.FacilityHoursHistoryResponse;
import com.cpmss.property.facilitymanager.FacilityManager;
import com.cpmss.property.facilitymanager.FacilityManagerRepository;
import com.cpmss.property.facilitymanager.dto.CreateFacilityManagerRequest;
import com.cpmss.property.facilitymanager.dto.FacilityManagerResponse;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final FacilityHoursHistoryRepository hoursHistoryRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final PersonRepository personRepository;
    private final FacilityMapper mapper;
    private final FacilityRules rules = new FacilityRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository               facility data access
     * @param buildingRepository       building data access (for FK lookup)
     * @param companyRepository        company data access (for FK lookup)
     * @param hoursHistoryRepository   hours history data access
     * @param facilityManagerRepository manager assignment data access
     * @param personRepository         person data access (for manager lookup)
     * @param mapper                   entity-DTO mapper
     */
    public FacilityService(FacilityRepository repository,
                           BuildingRepository buildingRepository,
                           CompanyRepository companyRepository,
                           FacilityHoursHistoryRepository hoursHistoryRepository,
                           FacilityManagerRepository facilityManagerRepository,
                           PersonRepository personRepository,
                           FacilityMapper mapper) {
        this.repository = repository;
        this.buildingRepository = buildingRepository;
        this.companyRepository = companyRepository;
        this.hoursHistoryRepository = hoursHistoryRepository;
        this.facilityManagerRepository = facilityManagerRepository;
        this.personRepository = personRepository;
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

    // ── Hours History Sub-Resource ──────────────────────────────────────

    /**
     * Adds a hours history entry to a facility.
     *
     * @param facilityId the facility's UUID
     * @param request    the hours details
     * @return the created hours history response
     * @throws ResourceNotFoundException if the facility does not exist
     */
    @Transactional
    public FacilityHoursHistoryResponse addHoursHistory(UUID facilityId,
                                                        CreateFacilityHoursHistoryRequest request) {
        Facility facility = repository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", facilityId));

        FacilityHoursHistory history = new FacilityHoursHistory();
        history.setFacility(facility);
        history.setEffectiveDate(request.effectiveDate());
        history.setOpeningTime(request.openingTime());
        history.setClosingTime(request.closingTime());
        history.setOperatingHours(request.operatingHours());
        history = hoursHistoryRepository.save(history);
        log.info("Hours history added: facility={}, effective={}",
                facilityId, request.effectiveDate());
        return toHoursResponse(history);
    }

    /**
     * Retrieves all hours history entries for a facility.
     *
     * @param facilityId the facility's UUID
     * @return hours history entries, most recent first
     * @throws ResourceNotFoundException if the facility does not exist
     */
    @Transactional(readOnly = true)
    public List<FacilityHoursHistoryResponse> getHoursHistory(UUID facilityId) {
        if (!repository.existsById(facilityId)) {
            throw new ResourceNotFoundException("Facility", facilityId);
        }
        return hoursHistoryRepository.findByFacilityIdOrderByEffectiveDateDesc(facilityId)
                .stream().map(this::toHoursResponse).toList();
    }

    // ── Manager Assignment Sub-Resource ─────────────────────────────────

    /**
     * Assigns a manager to a facility.
     *
     * @param facilityId the facility's UUID
     * @param request    the manager assignment details
     * @return the created manager assignment response
     * @throws ResourceNotFoundException if the facility or person does not exist
     */
    @Transactional
    public FacilityManagerResponse assignManager(UUID facilityId,
                                                  CreateFacilityManagerRequest request) {
        Facility facility = repository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", facilityId));
        Person manager = personRepository.findById(request.managerId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.managerId()));

        // Close current active assignment
        facilityManagerRepository.findByFacilityIdOrderByManagementStartDateDesc(facilityId)
                .stream().filter(fm -> fm.getManagementEndDate() == null).findFirst()
                .ifPresent(current -> {
                    current.setManagementEndDate(request.managementStartDate().minusDays(1));
                    facilityManagerRepository.save(current);
                });

        FacilityManager assignment = new FacilityManager();
        assignment.setFacility(facility);
        assignment.setManager(manager);
        assignment.setManagementStartDate(request.managementStartDate());
        assignment = facilityManagerRepository.save(assignment);
        log.info("Manager assigned: facility={}, manager={}, startDate={}",
                facilityId, request.managerId(), request.managementStartDate());
        return toManagerResponse(assignment);
    }

    /**
     * Retrieves all manager assignments for a facility.
     *
     * @param facilityId the facility's UUID
     * @return manager assignments, most recent first
     * @throws ResourceNotFoundException if the facility does not exist
     */
    @Transactional(readOnly = true)
    public List<FacilityManagerResponse> getManagers(UUID facilityId) {
        if (!repository.existsById(facilityId)) {
            throw new ResourceNotFoundException("Facility", facilityId);
        }
        return facilityManagerRepository.findByFacilityIdOrderByManagementStartDateDesc(facilityId)
                .stream().map(this::toManagerResponse).toList();
    }

    private FacilityHoursHistoryResponse toHoursResponse(FacilityHoursHistory h) {
        return new FacilityHoursHistoryResponse(
                h.getFacility().getId(), h.getEffectiveDate(),
                h.getOpeningTime(), h.getClosingTime(), h.getOperatingHours());
    }

    private FacilityManagerResponse toManagerResponse(FacilityManager m) {
        return new FacilityManagerResponse(
                m.getFacility().getId(), m.getManager().getId(),
                m.getManagementStartDate(), m.getManagementEndDate());
    }
}
