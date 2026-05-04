package com.cpmss.role;

import com.cpmss.common.PagedResponse;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.role.dto.CreateRoleRequest;
import com.cpmss.role.dto.RoleResponse;
import com.cpmss.role.dto.UpdateRoleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates role lifecycle operations.
 *
 * <p>Handles CRUD for Role entities. Delegates business rules
 * to {@link RoleRules} and data access to {@link RoleRepository}.
 *
 * @see RoleRules
 * @see RoleRepository
 */
@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final RoleRules rules = new RoleRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository role data access
     * @param mapper     entity-DTO mapper
     */
    public RoleService(RoleRepository repository, RoleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the role's UUID primary key
     * @return the matching role response
     * @throws ResourceNotFoundException if no role exists with this ID
     */
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return mapper.toResponse(role);
    }

    /**
     * Lists all roles with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of role DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<RoleResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new role.
     *
     * @param request the create request with the role name
     * @return the created role response
     */
    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        rules.validateNameUnique(request.roleName(), repository.existsByRoleName(request.roleName()));
        Role role = mapper.toEntity(request);
        role = repository.save(role);
        log.info("Role created: {}", role.getRoleName());
        return mapper.toResponse(role);
    }

    /**
     * Updates an existing role.
     *
     * @param id      the role's UUID
     * @param request the update request with the new name
     * @return the updated role response
     * @throws ResourceNotFoundException if no role exists with this ID
     */
    @Transactional
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        if (!role.getRoleName().equals(request.roleName())) {
            rules.validateNameUnique(request.roleName(), repository.existsByRoleName(request.roleName()));
        }
        role.setRoleName(request.roleName());
        role = repository.save(role);
        log.info("Role updated: {}", role.getRoleName());
        return mapper.toResponse(role);
    }

    /**
     * Deletes a role by ID.
     *
     * @param id the role's UUID
     * @throws ResourceNotFoundException if no role exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        repository.delete(role);
        log.info("Role deleted: {}", role.getRoleName());
    }
}
