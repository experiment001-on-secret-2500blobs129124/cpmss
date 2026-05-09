package com.cpmss.people.role;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.people.common.PeopleAccessRules;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.people.role.dto.CreateRoleRequest;
import com.cpmss.people.role.dto.RoleResponse;
import com.cpmss.people.role.dto.UpdateRoleRequest;
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
    private final CurrentUserService currentUserService;
    private final RoleRules rules = new RoleRules();
    private final PeopleAccessRules accessRules = new PeopleAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository role data access
     * @param mapper     entity-DTO mapper
     */
    public RoleService(RoleRepository repository, RoleMapper mapper,
                       CurrentUserService currentUserService) {
        this.repository = repository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the role's UUID primary key
     * @return the matching role response
     * @throws ApiException if no role exists with this ID
     */
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Role role = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.ROLE_NOT_FOUND));
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
        accessRules.requireHrAuthority(currentUserService.currentUser());
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
        accessRules.requireHrAuthority(currentUserService.currentUser());
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
     * @throws ApiException if no role exists with this ID
     */
    @Transactional
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Role role = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.ROLE_NOT_FOUND));
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
     * @throws ApiException if no role exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requireHrAuthority(currentUserService.currentUser());
        Role role = repository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.ROLE_NOT_FOUND));
        repository.delete(role);
        log.info("Role deleted: {}", role.getRoleName());
    }
}
