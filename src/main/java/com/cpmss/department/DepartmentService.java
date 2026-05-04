package com.cpmss.department;

import com.cpmss.common.PagedResponse;
import com.cpmss.department.dto.CreateDepartmentRequest;
import com.cpmss.department.dto.DepartmentResponse;
import com.cpmss.department.dto.UpdateDepartmentRequest;
import com.cpmss.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DepartmentMapper mapper;
    private final DepartmentRules rules = new DepartmentRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository department data access
     * @param mapper     entity-DTO mapper
     */
    public DepartmentService(DepartmentRepository repository, DepartmentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a department by its unique identifier.
     *
     * @param id the department's UUID primary key
     * @return the matching department response
     * @throws ResourceNotFoundException if no department exists with this ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getById(UUID id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
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
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new department.
     *
     * @param request the create request with the department name
     * @return the created department response
     */
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
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
     * @throws ResourceNotFoundException if no department exists with this ID
     */
    @Transactional
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));

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
     * @throws ResourceNotFoundException if no department exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
        repository.delete(department);
        log.info("Department deleted: {}", department.getDepartmentName());
    }
}
