package com.cpmss.organization.department;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.property.building.Building;
import com.cpmss.property.building.BuildingRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.organization.department.dto.CreateDepartmentRequest;
import com.cpmss.organization.department.dto.DepartmentResponse;
import com.cpmss.organization.department.dto.UpdateDepartmentRequest;
import com.cpmss.organization.departmentlocationhistory.DepartmentLocationHistory;
import com.cpmss.organization.departmentlocationhistory.DepartmentLocationHistoryRepository;
import com.cpmss.organization.departmentlocationhistory.dto.CreateDeptLocationHistoryRequest;
import com.cpmss.organization.departmentlocationhistory.dto.DeptLocationHistoryResponse;
import com.cpmss.organization.departmentmanagers.DepartmentManagers;
import com.cpmss.organization.departmentmanagers.DepartmentManagersRepository;
import com.cpmss.organization.departmentmanagers.dto.CreateDeptManagerRequest;
import com.cpmss.organization.departmentmanagers.dto.DeptManagerResponse;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationAccessRules;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.platform.exception.ApiException;
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
 * Orchestrates department lifecycle operations.
 *
 * <p>Handles CRUD for Department entities. Delegates business rules
 * to {@link DepartmentRules} and data access to {@link DepartmentRepository}.
 *
 * @see DepartmentRules
 * @see DepartmentRepository
 */
@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository repository;
    private final BuildingRepository buildingRepository;
    private final DepartmentLocationHistoryRepository locationHistoryRepository;
    private final DepartmentManagersRepository managersRepository;
    private final PersonRepository personRepository;
    private final DepartmentMapper mapper;
    private final CurrentUserService currentUserService;
    private final DepartmentScopeService departmentScopeService;
    private final DepartmentRules rules = new DepartmentRules();
    private final OrganizationAccessRules accessRules = new OrganizationAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository               department data access
     * @param buildingRepository       building data access (for location FK)
     * @param locationHistoryRepository location history data access
     * @param managersRepository       manager assignment data access
     * @param personRepository         person data access (for manager lookup)
     * @param mapper                   entity-DTO mapper
     */
    public DepartmentService(DepartmentRepository repository,
                             BuildingRepository buildingRepository,
                             DepartmentLocationHistoryRepository locationHistoryRepository,
                             DepartmentManagersRepository managersRepository,
                             PersonRepository personRepository,
                             DepartmentMapper mapper,
                             CurrentUserService currentUserService,
                             DepartmentScopeService departmentScopeService) {
        this.repository = repository;
        this.buildingRepository = buildingRepository;
        this.locationHistoryRepository = locationHistoryRepository;
        this.managersRepository = managersRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
        this.departmentScopeService = departmentScopeService;
    }

    /**
     * Retrieves a department by its unique identifier.
     *
     * @param id the department's UUID primary key
     * @return the matching department response
     * @throws ApiException if no department exists with this ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getById(UUID id) {
        accessRules.requireCanViewDepartment(
                currentUserService.currentUser(), id, departmentScopeService);
        Department department = repository.findById(id)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        return mapper.toResponse(department);
    }

    /**
     * Lists all departments with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of department DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<DepartmentResponse> listAll(Pageable pageable) {
        CurrentUser user = currentUserService.currentUser();
        List<DepartmentResponse> content = repository.findAll(pageable).getContent()
                .stream()
                .filter(department -> canViewDepartment(user, department.getId()))
                .map(mapper::toResponse)
                .toList();
        return new PagedResponse<>(
                content, content.size(), 1, pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Creates a new department.
     *
     * @param request the create request with the department name
     * @return the created department response
     */
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        rules.validateNameUnique(
                request.departmentName(),
                repository.existsByDepartmentName(request.departmentName()));

        Department department = mapper.toEntity(request);
        department = repository.save(department);
        log.info("Department created: {}", department.getDepartmentName());
        return mapper.toResponse(department);
    }

    /**
     * Updates an existing department.
     *
     * @param id      the department's UUID
     * @param request the update request with the new name
     * @return the updated department response
     * @throws ApiException if no department exists with this ID
     */
    @Transactional
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        Department department = repository.findById(id)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        if (!department.getDepartmentName().equals(request.departmentName())) {
            rules.validateNameUnique(
                    request.departmentName(),
                    repository.existsByDepartmentName(request.departmentName()));
        }

        department.setDepartmentName(request.departmentName());
        department = repository.save(department);
        log.info("Department updated: {}", department.getDepartmentName());
        return mapper.toResponse(department);
    }

    /**
     * Deletes a department by ID.
     *
     * @param id the department's UUID
     * @throws ApiException if no department exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        Department department = repository.findById(id)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        repository.delete(department);
        log.info("Department deleted: {}", department.getDepartmentName());
    }

    // ── Location History Sub-Resource ───────────────────────────────────

    /**
     * Adds a location history entry to a department.
     *
     * <p>Closes the currently active location (sets end date) before
     * creating the new one. SCD Type 2 pattern.
     *
     * @param departmentId the department's UUID
     * @param request      the location details
     * @return the created location history response
     * @throws ApiException if the department or building does not exist
     */
    @Transactional
    public DeptLocationHistoryResponse addLocationHistory(
            UUID departmentId, CreateDeptLocationHistoryRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        Department department = repository.findById(departmentId)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        // Close current active location
        locationHistoryRepository
                .findByDepartmentIdOrderByLocationStartDateInBuildingDesc(departmentId)
                .stream().filter(h -> h.getLocationEndDateInBuilding() == null).findFirst()
                .ifPresent(current -> {
                    current.setLocationEndDateInBuilding(
                            request.locationStartDate().minusDays(1));
                    locationHistoryRepository.save(current);
                });

        DepartmentLocationHistory history = new DepartmentLocationHistory();
        history.setDepartment(department);
        history.setLocationStartDateInBuilding(request.locationStartDate());
        history.setBuilding(building);
        history = locationHistoryRepository.save(history);
        log.info("Location history added: dept={}, building={}, start={}",
                departmentId, request.buildingId(), request.locationStartDate());
        return toLocationResponse(history);
    }

    /**
     * Retrieves all location history entries for a department.
     *
     * @param departmentId the department's UUID
     * @return location history entries, most recent first
     * @throws ApiException if the department does not exist
     */
    @Transactional(readOnly = true)
    public List<DeptLocationHistoryResponse> getLocationHistory(UUID departmentId) {
        accessRules.requireCanViewDepartment(
                currentUserService.currentUser(), departmentId, departmentScopeService);
        if (!repository.existsById(departmentId)) {
            throw new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND);
        }
        return locationHistoryRepository
                .findByDepartmentIdOrderByLocationStartDateInBuildingDesc(departmentId)
                .stream().map(this::toLocationResponse).toList();
    }

    // ── Manager Assignment Sub-Resource ─────────────────────────────────

    /**
     * Assigns a manager to a department.
     *
     * <p>Closes the currently active manager (sets end date) before
     * creating the new assignment. SCD Type 2 pattern.
     *
     * @param departmentId the department's UUID
     * @param request      the manager assignment details
     * @return the created manager assignment response
     * @throws ApiException if the department or person does not exist
     */
    @Transactional
    public DeptManagerResponse assignManager(UUID departmentId,
                                              CreateDeptManagerRequest request) {
        accessRules.requireOrganizationAdministrator(currentUserService.currentUser());
        Department department = repository.findById(departmentId)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));
        Person manager = personRepository.findById(request.managerId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        // Close current active assignment
        managersRepository.findByDepartmentIdOrderByManagementStartDateDesc(departmentId)
                .stream().filter(m -> m.getManagementEndDate() == null).findFirst()
                .ifPresent(current -> {
                    current.setManagementEndDate(request.managementStartDate().minusDays(1));
                    managersRepository.save(current);
                });

        DepartmentManagers assignment = new DepartmentManagers();
        assignment.setDepartment(department);
        assignment.setManager(manager);
        assignment.setManagementStartDate(request.managementStartDate());
        assignment = managersRepository.save(assignment);
        log.info("Manager assigned: dept={}, manager={}, start={}",
                departmentId, request.managerId(), request.managementStartDate());
        return toManagerResponse(assignment);
    }

    /**
     * Retrieves all manager assignments for a department.
     *
     * @param departmentId the department's UUID
     * @return manager assignments, most recent first
     * @throws ApiException if the department does not exist
     */
    @Transactional(readOnly = true)
    public List<DeptManagerResponse> getManagers(UUID departmentId) {
        accessRules.requireCanViewDepartment(
                currentUserService.currentUser(), departmentId, departmentScopeService);
        if (!repository.existsById(departmentId)) {
            throw new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND);
        }
        return managersRepository.findByDepartmentIdOrderByManagementStartDateDesc(departmentId)
                .stream().map(this::toManagerResponse).toList();
    }


    /**
     * Retrieves the current manager assignment for a department.
     *
     * @param departmentId the department UUID
     * @return current manager assignment
     * @throws ApiException if the department or current manager assignment does not exist
     */
    @Transactional(readOnly = true)
    public DeptManagerResponse getCurrentManager(UUID departmentId) {
        CurrentUser user = currentUserService.currentUser();
        accessRules.requireCanViewDepartment(user, departmentId, departmentScopeService);
        if (!repository.existsById(departmentId)) {
            throw new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND);
        }
        return managersRepository
                .findFirstByDepartmentIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(departmentId)
                .map(this::toManagerResponse)
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_MANAGER_NOT_FOUND));
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private boolean canViewDepartment(CurrentUser user, UUID departmentId) {
        try {
            accessRules.requireCanViewDepartment(user, departmentId, departmentScopeService);
            return true;
        } catch (ApiException ex) {
            return false;
        }
    }

    private DeptLocationHistoryResponse toLocationResponse(DepartmentLocationHistory h) {
        return new DeptLocationHistoryResponse(
                h.getDepartment().getId(), h.getLocationStartDateInBuilding(),
                h.getLocationEndDateInBuilding(), h.getBuilding().getId());
    }

    private DeptManagerResponse toManagerResponse(DepartmentManagers m) {
        return new DeptManagerResponse(
                m.getDepartment().getId(), m.getManager().getId(),
                m.getManagementStartDate(), m.getManagementEndDate());
    }
}
