package com.cpmss.identity.auth;

import com.cpmss.people.common.EmailAddress;
import com.cpmss.platform.exception.ForbiddenException;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.platform.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves the authenticated AppUser for service-level authorization checks.
 *
 * <p>The JWT establishes the request identity. This service reloads the
 * active account from the database so ownership rules use the current role,
 * person link, and active status instead of trusting stale token details.
 *
 * @see CurrentUser
 * @see AppUserRepository
 */
@Service
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    /**
     * Constructs the resolver with user-account data access.
     *
     * @param appUserRepository AppUser repository
     */
    public CurrentUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Resolves the current authenticated user from the security context.
     *
     * @return the active current user
     * @throws ForbiddenException if no authenticated user exists
     * @throws ResourceNotFoundException if the authenticated account is no longer active
     */
    @Transactional(readOnly = true)
    public CurrentUser currentUser() {
        String email = AuthUtils.getCurrentUserEmail()
                .orElseThrow(() -> new ForbiddenException("No authenticated user"));
        EmailAddress loginEmail = EmailAddress.of(email);
        AppUser user = appUserRepository.findByEmailAndActiveTrue(loginEmail)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser", loginEmail.value()));
        return new CurrentUser(user.getId(), user.getPersonId(), user.getSystemRole(), user.getEmail());
    }
}
