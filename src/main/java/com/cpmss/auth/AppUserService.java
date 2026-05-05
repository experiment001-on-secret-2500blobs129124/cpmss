package com.cpmss.auth;

import com.cpmss.auth.dto.AppUserResponse;
import com.cpmss.auth.dto.CreateAppUserRequest;
import com.cpmss.auth.dto.UpdateUserRoleRequest;
import com.cpmss.auth.dto.UpdateUserStatusRequest;
import com.cpmss.common.PagedResponse;
import com.cpmss.exception.ForbiddenException;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates AppUser account management operations.
 *
 * <p>Implements the cascading authority chain defined in
 * REQUIREMENTS.md § 3 — who can create, promote, and deactivate
 * user accounts. Auth-only operations (login, setup, refresh)
 * remain in {@link AuthService}.
 *
 * @see AppUserRules
 * @see AppUserRepository
 * @see AuthService
 */
@Service
public class AppUserService {

    private static final Logger log = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper mapper;
    private final AppUserRules rules = new AppUserRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository      user data access
     * @param passwordEncoder BCrypt password encoder
     * @param mapper          entity-DTO mapper
     */
    public AppUserService(AppUserRepository repository,
                          PasswordEncoder passwordEncoder,
                          AppUserMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    /**
     * Retrieves a user account by its unique identifier.
     *
     * @param id the user's UUID primary key
     * @return the matching user response
     * @throws ResourceNotFoundException if no user exists with this ID
     */
    @Transactional(readOnly = true)
    public AppUserResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all user accounts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of user DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<AppUserResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new AppUser account with authority validation.
     *
     * <p>The actor's role determines which target roles they can assign.
     * See REQUIREMENTS.md § 3 for the full authority chain.
     *
     * @param request the create request with email, password, role, and optional personId
     * @return the created user response
     * @throws ForbiddenException if the actor lacks authority
     * @throws com.cpmss.exception.BusinessException if the email is already registered
     */
    @Transactional
    public AppUserResponse create(CreateAppUserRequest request) {
        SystemRole actorRole = getCurrentUserRole();

        rules.validateEmailUnique(request.email(),
                repository.existsByEmailIgnoreCase(request.email()));
        rules.validateAuthorityLevel(actorRole, request.systemRole());
        rules.validateDeptManagerCanOnlyCreateStaffOrGuard(actorRole, request.systemRole());

        AppUser user = AppUser.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(request.systemRole())
                .active(true)
                .forcePasswordChange(true)
                .personId(request.personId())
                .build();
        user = repository.save(user);
        log.info("User account created: {} with role {}", user.getEmail(), user.getSystemRole());
        return mapper.toResponse(user);
    }

    /**
     * Registers a new APPLICANT account (public, no auth required).
     *
     * <p>Used by job seekers to self-register on the portal.
     * See REQUIREMENTS.md US-10 for the full workflow.
     *
     * @param request the create request (systemRole is forced to APPLICANT)
     * @return the created user response
     * @throws com.cpmss.exception.BusinessException if the email is already registered
     */
    @Transactional
    public AppUserResponse register(CreateAppUserRequest request) {
        rules.validateEmailUnique(request.email(),
                repository.existsByEmailIgnoreCase(request.email()));

        AppUser user = AppUser.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(SystemRole.APPLICANT)
                .active(true)
                .forcePasswordChange(false)
                .personId(request.personId())
                .build();
        user = repository.save(user);
        log.info("Applicant self-registered: {}", user.getEmail());
        return mapper.toResponse(user);
    }

    /**
     * Changes a user's system role with authority validation.
     *
     * <p>Enforces: cannot change own role, cannot promote to a role
     * at or above the actor's own level (unless ADMIN).
     *
     * @param userId  the target user's UUID
     * @param request the new role to assign
     * @return the updated user response
     * @throws ResourceNotFoundException if no user exists with this ID
     * @throws ForbiddenException if the actor lacks authority
     */
    @Transactional
    public AppUserResponse updateRole(UUID userId, UpdateUserRoleRequest request) {
        UUID actorId = getCurrentUserId();
        SystemRole actorRole = getCurrentUserRole();

        rules.validateCannotChangeOwnRole(actorId, userId);
        rules.validateAuthorityLevel(actorRole, request.systemRole());

        AppUser user = findOrThrow(userId);
        user.setSystemRole(request.systemRole());
        user = repository.save(user);
        log.info("User {} role changed to {} by {}",
                user.getEmail(), user.getSystemRole(), actorId);
        return mapper.toResponse(user);
    }

    /**
     * Activates or deactivates a user account.
     *
     * <p>Enforces: cannot deactivate your own account.
     *
     * @param userId  the target user's UUID
     * @param request the new active status
     * @return the updated user response
     * @throws ResourceNotFoundException if no user exists with this ID
     * @throws ForbiddenException if the actor tries to deactivate themselves
     */
    @Transactional
    public AppUserResponse updateStatus(UUID userId, UpdateUserStatusRequest request) {
        UUID actorId = getCurrentUserId();

        if (!request.active()) {
            rules.validateCannotDeactivateSelf(actorId, userId);
        }

        AppUser user = findOrThrow(userId);
        user.setActive(request.active());
        user = repository.save(user);
        log.info("User {} active status changed to {} by {}",
                user.getEmail(), user.isActive(), actorId);
        return mapper.toResponse(user);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private AppUser findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser", id));
    }

    /**
     * Extracts the current authenticated user's ID by looking up
     * their email from the security context.
     */
    private UUID getCurrentUserId() {
        String email = AuthUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ForbiddenException("No authenticated user"));
        AppUser actor = repository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser", email));
        return actor.getId();
    }

    /**
     * Extracts the current authenticated user's system role from
     * the Spring Security granted authorities.
     */
    private SystemRole getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) {
            throw new ForbiddenException("No authenticated user");
        }
        String authority = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("No role found in security context"));
        return SystemRole.valueOf(authority.substring("ROLE_".length()));
    }
}
