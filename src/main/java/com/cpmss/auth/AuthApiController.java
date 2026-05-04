package com.cpmss.auth;

import com.cpmss.auth.dto.LoginRequest;
import com.cpmss.auth.dto.LoginResponse;
import com.cpmss.auth.dto.RefreshRequest;
import com.cpmss.auth.dto.SetupRequest;
import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 *
 * <p>Handles system bootstrap ({@code POST /setup}), user login
 * ({@code POST /api/v1/auth/login}), and token refresh
 * ({@code POST /api/v1/auth/refresh}).
 *
 * @see AuthService
 */
@RestController
public class AuthApiController {

    private final AuthService authService;

    /**
     * Constructs the controller with the auth service dependency.
     *
     * @param authService authentication orchestration service
     */
    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Bootstraps the first admin user.
     *
     * <p>Only succeeds when the {@code App_User} table is empty.
     * Returns 404 permanently after the first admin is created.
     *
     * @param request the admin's email and password
     * @return 201 Created with the JWT pair
     */
    @PostMapping(ApiPaths.SETUP)
    public ResponseEntity<ApiResponse<LoginResponse>> setup(
            @Valid @RequestBody SetupRequest request) {
        LoginResponse response = authService.setup(request);
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    /**
     * Authenticates a user and returns a JWT pair.
     *
     * @param request the login credentials (email + password)
     * @return 200 OK with access and refresh tokens
     */
    @PostMapping(ApiPaths.AUTH_LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Issues a new JWT pair using a valid refresh token.
     *
     * @param request the refresh token
     * @return 200 OK with new access and refresh tokens
     */
    @PostMapping(ApiPaths.AUTH_REFRESH)
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
