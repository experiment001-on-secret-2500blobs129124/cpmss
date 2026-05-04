package com.cpmss.staffposition;

import com.cpmss.common.PagedResponse;
import com.cpmss.department.Department;
import com.cpmss.department.DepartmentRepository;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.staffposition.dto.CreateStaffPositionRequest;
import com.cpmss.staffposition.dto.StaffPositionResponse;
import com.cpmss.staffposition.dto.UpdateStaffPositionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates staff position lifecycle operations.
 *
 * <p>Handles CRUD for staff positions. Each position is scoped
 * to a {@link Department}.
 *
 * @see StaffPositionRules
 * @see StaffPositionRepository
 */
@Service
public class StaffPositionService {

    private static final Logger log = LoggerFactory.getLogger(StaffPositionService.class);

    private final StaffPositionRepository repository;
    private final DepartmentRepository departmentRepository;
    private final StaffPositionMapper mapper;
    private final StaffPositionRules rules = new StaffPositionRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository           staff position data access
     * @param departmentRepository department data access (FK lookup)
     * @param mapper               entity-DTO mapper
     */
    public StaffPositionService(StaffPositionRepository repository,
                                DepartmentRepository departmentRepository,
                                StaffPositionMapper mapper) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a staff position by its unique identifier.
     *
     * @param id the position's UUID primary key
     * @return the matching position response
     * @throws ResourceNotFoundException if no position exists with this ID
     */
    @Transactional(readOnly = true)
    public StaffPositionResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPosition", id)));
    }

    /**
     * Lists all staff positions with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of staff position DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<StaffPositionResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new staff position.
     *
     * @param request the create request with position name and department ID
     * @return the created position response
     * @throws ResourceNotFoundException if the department does not exist
     */
    @Transactional
    public StaffPositionResponse create(CreateStaffPositionRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

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
     * @param id      the position's UUID
     * @param request the update request with new values
     * @return the updated position response
     * @throws ResourceNotFoundException if the position or department does not exist
     */
    @Transactional
    public StaffPositionResponse update(UUID id, UpdateStaffPositionRequest request) {
        StaffPosition position = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPosition", id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

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
     * @throws ResourceNotFoundException if no position exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        StaffPosition position = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffPosition", id));
        repository.delete(position);
        log.info("StaffPosition deleted: {}", position.getPositionName());
    }
}
