package com.cpmss.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Supplies the current user's email to Spring Data's auditing infrastructure.
 *
 * <p>Called automatically on every entity save to populate {@code createdBy}
 * and {@code updatedBy} on {@link com.cpmss.common.BaseEntity}. Returns empty
 * when no authenticated user exists, which leaves those fields {@code null}.
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    /**
     * Returns the email of the currently authenticated user.
     *
     * @return {@code Optional} containing the user's email, or empty for
     *         anonymous or unauthenticated requests
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return Optional.of(auth.getName());
    }
}
