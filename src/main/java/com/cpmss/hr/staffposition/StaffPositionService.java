package com.cpmss.hr.staffposition;

import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.staffposition.dto.CreatePositionSalaryHistoryRequest;
import com.cpmss.hr.staffposition.dto.CreateStaffPositionRequest;
import com.cpmss.hr.staffposition.dto.PositionSalaryHistoryResponse;
import com.cpmss.hr.staffposition.dto.StaffPositionResponse;
import com.cpmss.hr.staffposition.dto.UpdateStaffPositionRequest;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistory;
import com.cpmss.hr.staffpositionhistory.StaffPositionHistoryRepository;
import com.cpmss.hr.staffpositionhistory.dto.CreateStaffPositionHistoryRequest;
import com.cpmss.hr.staffpositionhistory.dto.StaffPositionHistoryResponse;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates staff position lifecycle operations.
 *
 * <p>Handles CRUD for staff positions plus the staff assignment and position
 * salary-band history workflows owned by HR.
 *
 * @see StaffPositionRules
 * @see StaffPositionRepository
 */
@Service
public class StaffPositionService {

    private static final Logger log = LoggerFactory.getLogger(StaffPositionService.class);

    private final StaffPositionRepository repository;
    private final PositionSalaryHistoryRepository salaryHistoryRepository;
    private final StaffPositionHistoryRepository positionHistoryRepository;
    private final DepartmentRepository departmentRepository;
    private final PersonRepository personRepository;
    private final StaffPositionMapper mapper;
    private final CurrentUserService currentUserService;
    private final StaffPositionRules rules = new StaffPositionRules();
    private final HrAccessRules accessRules = new HrAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository staff position data access
     * @param salaryHistoryRepository position salary-band history data access
     * @param positionHistoryRepository staff position assignment history data access
     * @param departmentRepository department data access
     * @param personRepository person data access
     * @param mapper entity-DTO mapper
     * @param currentUserService current authenticated user lookup
     */
    public StaffPositionService(StaffPositionRepository repository,
                                PositionSalaryHistoryRepository salaryHistoryRepository,
                                StaffPositionHistoryRepository positionHistoryRepository,
                                DepartmentRepository departmentRepository,
                                PersonRepository personRepository,
                                StaffPositionMapper mapper,
                                CurrentUserService currentUserService) {
        this.repository = repository;
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.positionHistoryRepository = positionHistoryRepository;
        this.departmentRepository = departmentRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a staff position by its unique identifier.
     *
     * @param id the position's UUID primary key
     * @return the matching position response
     * @throws ApiException if no position exists with this ID
     */
    @Transactional(readOnly = true)
    public StaffPositionResponse getById(UUID id) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND)));
    }

    /**
     * Lists all staff positions with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of staff position DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<StaffPositionResponse> listAll(Pageable pageable) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new staff position.
     *
     * @param request the create request with position name and department ID
     * @return the created position response
     * @throws ApiException if the department does not exist
     */
    @Transactional
    public StaffPositionResponse create(CreateStaffPositionRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        StaffPosition position = StaffPosition.builder()
                .positionName(request.positionName())
                .department(department)
                .build();
        position = repository.save(position);
        log.info("StaffPosition created: {}", position.getPositionName());
        return mapper.toResponse(position);
    }

    /**
     * Updates an existing staff position.
     *
     * @param id the position's UUID
     * @param request the update request with new values
     * @return the updated position response
     * @throws ApiException if the position or department does not exist
     */
    @Transactional
    public StaffPositionResponse update(UUID id, UpdateStaffPositionRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        StaffPosition position = repository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        position.setPositionName(request.positionName());
        position.setDepartment(department);
        position = repository.save(position);
        log.info("StaffPosition updated: {}", position.getPositionName());
        return mapper.toResponse(position);
    }

    /**
     * Deletes a staff position by ID.
     *
     * @param id the position's UUID
     * @throws ApiException if no position exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        StaffPosition position = repository.findById(id)
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));
        repository.delete(position);
        log.info("StaffPosition deleted: {}", position.getPositionName());
    }

    /**
     * Assigns a staff member to a new position and closes the previous active
     * assignment when one exists.
     *
     * @param request assignment request
     * @return created position history row
     */
    @Transactional
    public StaffPositionHistoryResponse assignStaffPosition(
            CreateStaffPositionHistoryRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        if (positionHistoryRepository.existsByPersonIdAndPositionIdAndEffectiveDate(
                request.personId(), request.positionId(), request.effectiveDate())) {
            throw new ApiException(HrErrorCode.STAFF_POSITION_ASSIGNMENT_DUPLICATE);
        }

        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        StaffPosition position = repository.findById(request.positionId())
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));
        Person authorizedBy = request.authorizedById() != null
                ? personRepository.findById(request.authorizedById())
                        .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND))
                : null;

        positionHistoryRepository.findByPersonIdAndEndDateIsNull(request.personId())
                .ifPresent(current -> closeCurrentAssignment(current, request));

        StaffPositionHistory history = new StaffPositionHistory();
        history.setPerson(person);
        history.setPosition(position);
        history.setEffectiveDate(request.effectiveDate());
        history.setAuthorizedBy(authorizedBy);
        history = positionHistoryRepository.save(history);
        log.info("Staff position assignment created: person={}, position={}, effective={}",
                request.personId(), request.positionId(), request.effectiveDate());
        return toPositionHistoryResponse(history);
    }

    /**
     * Lists staff position assignment history for a person.
     *
     * @param personId the staff member UUID
     * @return assignment history ordered by effective date descending
     */
    @Transactional(readOnly = true)
    public List<StaffPositionHistoryResponse> positionHistoryForPerson(UUID personId) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return positionHistoryRepository.findByPersonIdOrderByEffectiveDateDesc(personId)
                .stream()
                .map(this::toPositionHistoryResponse)
                .toList();
    }

    /**
     * Records a salary band for a position.
     *
     * @param positionId the position UUID
     * @param request the salary band request
     * @return created salary band history row
     */
    @Transactional
    public PositionSalaryHistoryResponse createPositionSalaryHistory(
            UUID positionId, CreatePositionSalaryHistoryRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        if (salaryHistoryRepository.existsByPositionIdAndSalaryEffectiveDate(
                positionId, request.salaryEffectiveDate())) {
            throw new ApiException(HrErrorCode.POSITION_SALARY_HISTORY_DUPLICATE);
        }
        StaffPosition position = repository.findById(positionId)
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));

        PositionSalaryHistory history = new PositionSalaryHistory();
        history.setPosition(position);
        history.setSalaryEffectiveDate(request.salaryEffectiveDate());
        history.setMaximumSalary(request.maximumSalary());
        history.setBaseDailyRate(request.baseDailyRate());
        history = salaryHistoryRepository.save(history);
        log.info("Position salary band created: position={}, effective={}",
                positionId, request.salaryEffectiveDate());
        return toPositionSalaryResponse(history);
    }

    /**
     * Lists salary history for a position.
     *
     * @param positionId the position UUID
     * @return salary-band history ordered by effective date descending
     */
    @Transactional(readOnly = true)
    public List<PositionSalaryHistoryResponse> positionSalaryHistory(UUID positionId) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        return salaryHistoryRepository.findByPositionIdOrderBySalaryEffectiveDateDesc(positionId)
                .stream()
                .map(this::toPositionSalaryResponse)
                .toList();
    }

    private void closeCurrentAssignment(StaffPositionHistory current,
                                        CreateStaffPositionHistoryRequest request) {
        if (!current.getEffectiveDate().isBefore(request.effectiveDate())) {
            throw new ApiException(HrErrorCode.STAFF_POSITION_ASSIGNMENT_OVERLAP);
        }
        if (request.authorizedById() == null) {
            throw new ApiException(HrErrorCode.STAFF_POSITION_AUTHORIZER_REQUIRED);
        }
        current.setEndDate(request.effectiveDate().minusDays(1));
        positionHistoryRepository.save(current);
    }

    private StaffPositionHistoryResponse toPositionHistoryResponse(StaffPositionHistory history) {
        return new StaffPositionHistoryResponse(
                history.getPerson().getId(),
                history.getPosition().getId(),
                history.getEffectiveDate(),
                history.getEndDate(),
                history.getAuthorizedBy() != null ? history.getAuthorizedBy().getId() : null);
    }

    private PositionSalaryHistoryResponse toPositionSalaryResponse(PositionSalaryHistory history) {
        return new PositionSalaryHistoryResponse(
                history.getPosition().getId(),
                history.getSalaryEffectiveDate(),
                history.getMaximumSalary(),
                history.getBaseDailyRate());
    }
}
