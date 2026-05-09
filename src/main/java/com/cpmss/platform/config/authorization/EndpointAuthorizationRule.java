package com.cpmss.platform.config.authorization;

import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Objects;

/**
 * Role rule for a protected HTTP endpoint.
 *
 * <p>Role names are stored without Spring Security's {@code ROLE_} prefix
 * because {@code hasAnyRole} adds that prefix at evaluation time.
 *
 * @param method      the protected HTTP method
 * @param pathPattern the security matcher pattern
 * @param roles       role names without the {@code ROLE_} prefix
 */
public record EndpointAuthorizationRule(
        HttpMethod method,
        String pathPattern,
        List<String> roles
) {
    /**
     * Creates an immutable endpoint authorization rule.
     *
     * @param method      the protected HTTP method
     * @param pathPattern the security matcher pattern
     * @param roles       role names without the {@code ROLE_} prefix
     */
    public EndpointAuthorizationRule {
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(pathPattern, "pathPattern must not be null");
        roles = List.copyOf(Objects.requireNonNull(roles, "roles must not be null"));
    }

    /**
     * Returns the allowed roles in the array shape expected by Spring Security.
     *
     * @return role names without the {@code ROLE_} prefix
     */
    public String[] roleArray() {
        return roles.toArray(String[]::new);
    }
}
