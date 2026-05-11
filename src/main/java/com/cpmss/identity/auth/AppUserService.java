package com.cpmss.identity.auth;

import com.cpmss.hr.application.Application;
import com.cpmss.hr.application.ApplicationRepository;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.staffposition.StaffPosition;
import com.cpmss.hr.staffposition.StaffPositionRepository;
import com.cpmss.identity.auth.dto.AppUserResponse;
import com.cpmss.identity.auth.dto.ApplicantRegistrationResponse;
import com.cpmss.identity.auth.dto.CreateAppUserRequest;
import com.cpmss.identity.auth.dto.RegisterApplicantRequest;
import com.cpmss.identity.auth.dto.UpdateUserRoleRequest;
import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.people.common.EmailAddress;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonEmail;
import com.cpmss.people.person.PersonPhone;
import com.cpmss.people.person.PersonRules;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final PersonRepository personRepository;
    private final StaffPositionRepository staffPositionRepository;
    private final ApplicationRepository applicationRepository;
    private final AppUserMapper mapper;
    private final CurrentUserService currentUserService;
    private final AppUserRules rules = new AppUserRules();
    private final AppUserAccessRules accessRules = new AppUserAccessRules();
    private final PersonRules personRules = new PersonRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository      user data access
     * @param passwordEncoder BCrypt password encoder
     * @param personRepository person data access for applicant profiles
     * @param staffPositionRepository staff position data access for applications
     * @param applicationRepository application data access
     * @param mapper          entity-DTO mapper
     */
    public AppUserService(AppUserRepository repository,
                          PasswordEncoder passwordEncoder,
                          PersonRepository personRepository,
                          StaffPositionRepository staffPositionRepository,
                          ApplicationRepository applicationRepository,
                          AppUserMapper mapper,
                          CurrentUserService currentUserService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.staffPositionRepository = staffPositionRepository;
        this.applicationRepository = applicationRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a user account by its unique identifier.
     *
     * @param id the user's UUID primary key
     * @return the matching user response
     * @throws ApiException if no user exists with this ID
     */
    @Transactional(readOnly = true)
    public AppUserResponse getById(UUID id) {
        accessRules.requireCanViewAccount(currentUserService.currentUser(), id);
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
        accessRules.requireAccountManager(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Finds a user account by email address.
     *
     * @param email the login email to search for
     * @return the matching user response
     * @throws ApiException if no active user exists with this email
     */
    @Transactional(readOnly = true)
    public AppUserResponse findByEmail(String email) {
        accessRules.requireAccountManager(currentUserService.currentUser());
        EmailAddress loginEmail = EmailAddress.of(email);
        AppUser user = repository.findByEmailAndActiveTrue(loginEmail)
                .orElseThrow(() -> new ApiException(IdentityErrorCode.USER_NOT_FOUND));
        return mapper.toResponse(user);
    }

    /**
     * Creates a new AppUser account with authority validation.
     *
     * <p>The actor's role determines which target roles they can assign.
     * See REQUIREMENTS.md § 3 for the full authority chain.
     *
     * @param request the create request with email, password, role, and optional personId
     * @return the created user response
     * @throws ApiException if the actor lacks authority
     * @throws ApiException if the email is already registered
     */
    @Transactional
    public AppUserResponse create(CreateAppUserRequest request) {
        accessRules.requireAccountCreator(currentUserService.currentUser());
        SystemRole actorRole = getCurrentUserRole();
        EmailAddress loginEmail = EmailAddress.of(request.email());

        rules.validateEmailUnique(loginEmail.value(), repository.existsByEmail(loginEmail));
        rules.validateAuthorityLevel(actorRole, request.systemRole());
        rules.validateProvisioningScope(actorRole, request.systemRole());
        rules.validateDeptManagerCanOnlyCreateStaffOrGuard(actorRole, request.systemRole());

        AppUser user = AppUser.builder()
                .email(loginEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(request.systemRole())
                .active(true)
                .forcePasswordChange(true)
                .personId(request.personId())
                .build();
        user = repository.save(user);
        log.info("user_account_created userId={} role={}", user.getId(), user.getSystemRole());
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
     * @throws ApiException if the email is already registered
     */
    @Transactional
    public AppUserResponse register(CreateAppUserRequest request) {
        EmailAddress loginEmail = EmailAddress.of(request.email());
        rules.validateEmailUnique(loginEmail.value(), repository.existsByEmail(loginEmail));

        AppUser user = AppUser.builder()
                .email(loginEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(SystemRole.APPLICANT)
                .active(true)
                .forcePasswordChange(false)
                .personId(null)
                .build();
        user = repository.save(user);
        log.info("applicant_account_registered userId={}", user.getId());
        return mapper.toResponse(user);
    }


    /**
     * Registers a new applicant, creates their Person profile, and records the
     * first job application in one transaction.
     *
     * @param request applicant registration and application details
     * @return created account, person, and application identifiers
     */
    @Transactional
    public ApplicantRegistrationResponse registerApplicant(RegisterApplicantRequest request) {
        EmailAddress loginEmail = EmailAddress.of(request.email());
        PassportNumber passportNo = PassportNumber.of(request.passportNo());
        rules.validateEmailUnique(loginEmail.value(), repository.existsByEmail(loginEmail));
        personRules.validatePassportUnique(
                passportNo, personRepository.existsByPassportNo(passportNo));
        StaffPosition position = staffPositionRepository.findById(request.positionId())
                .orElseThrow(() -> new ApiException(HrErrorCode.POSITION_NOT_FOUND));

        Person person = Person.builder()
                .passportNo(passportNo)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .isBlacklisted(false)
                .build();
        person.getEmails().add(new PersonEmail(loginEmail.value()));
        person.getPhones().add(new PersonPhone(request.countryCode(), request.phone()));
        person = personRepository.save(person);

        LocalDate applicationDate = request.applicationDate() != null
                ? request.applicationDate() : LocalDate.now();
        if (applicationRepository.existsByApplicantIdAndPositionIdAndApplicationDate(
                person.getId(), request.positionId(), applicationDate)) {
            throw new ApiException(HrErrorCode.APPLICATION_DUPLICATE);
        }

        AppUser user = AppUser.builder()
                .email(loginEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .systemRole(SystemRole.APPLICANT)
                .active(true)
                .forcePasswordChange(false)
                .personId(person.getId())
                .build();
        user = repository.save(user);

        Application application = new Application();
        application.setApplicant(person);
        application.setPosition(position);
        application.setApplicationDate(applicationDate);
        applicationRepository.save(application);

        log.info("applicant_registered_and_applied "
                        + "userId={} personId={} positionId={} applicationDate={}",
                user.getId(), person.getId(), position.getId(), applicationDate);
        return new ApplicantRegistrationResponse(
                mapper.toResponse(user), person.getId(), position.getId(), applicationDate);
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
     * @throws ApiException if no user exists with this ID
     * @throws ApiException if the actor lacks authority
     */
    @Transactional
    public AppUserResponse updateRole(UUID userId, UpdateUserRoleRequest request) {
        accessRules.requireAccountManager(currentUserService.currentUser());
        UUID actorId = getCurrentUserId();
        SystemRole actorRole = getCurrentUserRole();

        rules.validateCannotChangeOwnRole(actorId, userId);
        rules.validateAuthorityLevel(actorRole, request.systemRole());
        rules.validateProvisioningScope(actorRole, request.systemRole());

        AppUser user = findOrThrow(userId);
        user.setSystemRole(request.systemRole());
        user = repository.save(user);
        log.info("user_role_changed userId={} role={} actorId={}",
                user.getId(), user.getSystemRole(), actorId);
        return mapper.toResponse(user);
    }

    /**
     * Activates or deactivates a user account.
     *
     * <p>Enforces: cannot deactivate your own account.
     * Reactivation is allowed (e.g. employee returns, admin break-glass).
     *
     * @param userId the target user's UUID
     * @param active the new active status
     * @return the updated user response
     * @throws ApiException if no user exists with this ID
     * @throws ApiException if the actor tries to deactivate themselves
     */
    @Transactional
    public AppUserResponse updateStatus(UUID userId, boolean active) {
        accessRules.requireAccountManager(currentUserService.currentUser());
        UUID actorId = getCurrentUserId();

        if (!active) {
            rules.validateCannotDeactivateSelf(actorId, userId);
        }

        AppUser user = findOrThrow(userId);
        user.setActive(active);
        user = repository.save(user);
        log.info("user_status_changed userId={} active={} actorId={}",
                user.getId(), active, actorId);
        return mapper.toResponse(user);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private AppUser findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(IdentityErrorCode.USER_NOT_FOUND));
    }

    /**
     * Extracts the current authenticated user's ID by looking up
     * their email from the security context.
     */
    private UUID getCurrentUserId() {
        String email = AuthUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ApiException(IdentityErrorCode.NOT_AUTHENTICATED));
        EmailAddress loginEmail = EmailAddress.of(email);
        AppUser actor = repository.findByEmailAndActiveTrue(loginEmail)
                .orElseThrow(() -> new ApiException(IdentityErrorCode.USER_NOT_FOUND));
        return actor.getId();
    }

    /**
     * Extracts the current authenticated user's system role from
     * the Spring Security granted authorities.
     */
    private SystemRole getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) {
            throw new ApiException(IdentityErrorCode.NOT_AUTHENTICATED);
        }
        String authority = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() -> new ApiException(IdentityErrorCode.NO_ROLE_IN_CONTEXT));
        return SystemRole.valueOf(authority.substring("ROLE_".length()));
    }
}
