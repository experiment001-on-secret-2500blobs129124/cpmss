package com.cpmss.identity.auth;

import com.cpmss.identity.auth.dto.LoginRequest;
import com.cpmss.identity.auth.dto.LoginResponse;
import com.cpmss.identity.auth.dto.RefreshRequest;
import com.cpmss.identity.auth.dto.SetupRequest;
import com.cpmss.people.common.EmailAddress;
import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.platform.config.JwtUtils;
import com.cpmss.platform.exception.ApiException;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates authentication operations: setup, login, and token refresh.
 *
 * <p>Delegates business rule validation to {@link AuthRules} and
 * token operations to {@link JwtUtils}. Data access via
 * {@link AppUserRepository}.
 *
 * @see AuthRules
 * @see JwtUtils
 * @see AppUserRepository
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthRules rules = new AuthRules();

    /**
     * Constructs the auth service with required dependencies.
     *
     * @param repository      user data access
     * @param passwordEncoder BCrypt password encoder
     * @param jwtUtils        JWT token utility
     */
    public AuthService(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Creates the first admin account (bootstrap).
     *
     * <p>Only works when the {@code App_User} table is empty.
     * The created account has {@code force_password_change = true}.
     *
     * @param request the setup request with email and password
     * @return the JWT pair for the newly created admin
     * @throws ApiException if users already exist in the system
     */
    @Transactional
    public LoginResponse setup(SetupRequest request) {
        rules.validateSetupAllowed(repository.count());
        EmailAddress loginEmail = EmailAddress.of(request.email());

        AppUser admin = AppUser.builder()
                .email(loginEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(SystemRole.ADMIN)
                .active(true)
                .forcePasswordChange(true)
                .build();
        repository.save(admin);
        log.info("Bootstrap admin created: {}", admin.getEmail());

        return LoginResponse.bearer(
                jwtUtils.generateAccessToken(admin.getEmail(), admin.getSystemRole().name()),
                jwtUtils.generateRefreshToken(admin.getEmail())
        );
    }

    /**
     * Authenticates a user by email and password, returns a JWT pair.
     *
     * @param request the login credentials
     * @return a JWT pair (access + refresh tokens)
     * @throws ApiException if no active user with this email exists or
     *                      the password is incorrect
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        EmailAddress loginEmail = EmailAddress.of(request.email());
        AppUser user = repository.findByEmailAndActiveTrue(loginEmail)
                .orElseThrow(() -> new ApiException(IdentityErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(IdentityErrorCode.CREDENTIALS_INVALID);
        }

        log.info("User logged in: {}", user.getEmail());
        return LoginResponse.bearer(
                jwtUtils.generateAccessToken(user.getEmail(), user.getSystemRole().name()),
                jwtUtils.generateRefreshToken(user.getEmail())
        );
    }

    /**
     * Issues a new JWT pair using a valid refresh token.
     *
     * <p>Validates the refresh token, looks up the user, and generates
     * fresh access and refresh tokens.
     *
     * @param request the refresh request containing the refresh token
     * @return a new JWT pair
     * @throws ApiException if the token is invalid, not a refresh token,
     *                      or the user no longer exists or is inactive
     */
    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshRequest request) {
        Claims claims = jwtUtils.validateToken(request.refreshToken());
        if (claims == null || !jwtUtils.isRefreshToken(claims)) {
            throw new ApiException(IdentityErrorCode.TOKEN_INVALID);
        }

        String email = jwtUtils.getEmail(claims);
        EmailAddress loginEmail = EmailAddress.of(email);
        AppUser user = repository.findByEmailAndActiveTrue(loginEmail)
                .orElseThrow(() -> new ApiException(IdentityErrorCode.USER_NOT_FOUND));

        rules.validateAccountActive(user.isActive());

        return LoginResponse.bearer(
                jwtUtils.generateAccessToken(user.getEmail(), user.getSystemRole().name()),
                jwtUtils.generateRefreshToken(user.getEmail())
        );
    }
}
