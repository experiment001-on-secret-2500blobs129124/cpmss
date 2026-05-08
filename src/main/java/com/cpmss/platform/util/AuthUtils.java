package com.cpmss.platform.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Extracts the currently authenticated user from the Spring Security context.
 *
 * <p>Used in service methods for ownership checks and audit logging.
 */
public final class AuthUtils {

    private AuthUtils() {}

    /**
     * Returns the email of the currently authenticated user.
     *
     * @return {@code Optional} containing the user's email, or empty if
     *         no authenticated user exists in the current security context
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }
}
