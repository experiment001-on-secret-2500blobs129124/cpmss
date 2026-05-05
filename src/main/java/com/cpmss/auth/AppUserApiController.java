package com.cpmss.auth;

import com.cpmss.auth.dto.AppUserResponse;
import com.cpmss.auth.dto.CreateAppUserRequest;
import com.cpmss.auth.dto.UpdateUserRoleRequest;
import com.cpmss.auth.dto.UpdateUserStatusRequest;
import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for AppUser account management.
 *
 * <p>Exposes user creation, listing, role changes, and account
 * activation/deactivation under {@link ApiPaths#USERS}. Also
 * provides the public APPLICANT self-registration endpoint at
 * {@link ApiPaths#REGISTER}.
 *
 * <p>Auth-only endpoints (login, setup, refresh) remain in
 * {@link AuthApiController}.
 *
 * @see AppUserService
 */
@RestController
public class AppUserApiController {

    private final AppUserService appUserService;

    /**
     * Constructs the controller with the user service.
     *
     * @param appUserService user account orchestration service
     */
    public AppUserApiController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Lists all user accounts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated user list
     */
    @GetMapping(ApiPaths.USERS)
    public ResponseEntity<ApiResponse<PagedResponse<AppUserResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(appUserService.listAll(pageable)));
    }

    /**
     * Retrieves a single user account by ID.
     *
     * @param id the user UUID
     * @return 200 OK with the user
     */
    @GetMapping(ApiPaths.USERS_BY_ID)
    public ResponseEntity<ApiResponse<AppUserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(appUserService.getById(id)));
    }

    /**
     * Creates a new user account.
     *
     * <p>Authority checks are enforced in the service layer.
     *
     * @param request the user details and role
     * @return 201 Created with the new user
     */
    @PostMapping(ApiPaths.USERS)
    public ResponseEntity<ApiResponse<AppUserResponse>> create(
            @Valid @RequestBody CreateAppUserRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(appUserService.create(request)));
    }

    /**
     * Changes a user's system role.
     *
     * @param id      the target user UUID
     * @param request the new role
     * @return 200 OK with the updated user
     */
    @PutMapping(ApiPaths.USERS_ROLE)
    public ResponseEntity<ApiResponse<AppUserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(appUserService.updateRole(id, request)));
    }

    /**
     * Activates or deactivates a user account.
     *
     * @param id      the target user UUID
     * @param request the new active status
     * @return 200 OK with the updated user
     */
    @PutMapping(ApiPaths.USERS_STATUS)
    public ResponseEntity<ApiResponse<AppUserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(appUserService.updateStatus(id, request)));
    }

    /**
     * APPLICANT self-registration (public, no auth required).
     *
     * <p>See REQUIREMENTS.md US-10. The system role is always
     * forced to APPLICANT regardless of what the request contains.
     *
     * @param request the registration details
     * @return 201 Created with the new applicant account
     */
    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<ApiResponse<AppUserResponse>> register(
            @Valid @RequestBody CreateAppUserRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(appUserService.register(request)));
    }
}
