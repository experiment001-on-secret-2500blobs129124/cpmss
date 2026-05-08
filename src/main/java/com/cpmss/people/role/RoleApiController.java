package com.cpmss.people.role;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.people.role.dto.CreateRoleRequest;
import com.cpmss.people.role.dto.RoleResponse;
import com.cpmss.people.role.dto.UpdateRoleRequest;
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
 * REST controller for role CRUD operations.
 *
 * @see RoleService
 */
@RestController
public class RoleApiController {

    private final RoleService roleService;

    /**
     * Constructs the controller with the role service.
     *
     * @param roleService role orchestration service
     */
    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Lists all roles with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated role list
     */
    @GetMapping(ApiPaths.ROLES)
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponse>>> listAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(roleService.listAll(pageable)));
    }

    /**
     * Retrieves a single role by ID.
     *
     * @param id the role UUID
     * @return 200 OK with the role
     */
    @GetMapping(ApiPaths.ROLES_BY_ID)
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(roleService.getById(id)));
    }

    /**
     * Creates a new role.
     *
     * @param request the role name
     * @return 201 Created with the new role
     */
    @PostMapping(ApiPaths.ROLES)
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(roleService.create(request)));
    }

    /**
     * Updates an existing role.
     *
     * @param id      the role UUID
     * @param request the updated role name
     * @return 200 OK with the updated role
     */
    @PutMapping(ApiPaths.ROLES_BY_ID)
    public ResponseEntity<ApiResponse<RoleResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(roleService.update(id, request)));
    }

    /**
     * Deletes a role by ID.
     *
     * @param id the role UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.ROLES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
