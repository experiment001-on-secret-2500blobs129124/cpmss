package com.cpmss.platform.config.authorization;

import org.springframework.http.HttpMethod;

/**
 * Builds stable method/path keys for authorization coverage tests.
 *
 * <p>The key format is intentionally simple so assertion failures show the
 * missing or extra protected route directly.
 */
final class RouteKey {

    private RouteKey() {
    }

    /**
     * Builds a route key from an endpoint authorization rule.
     *
     * @param rule the authorization rule
     * @return a stable method/path key
     */
    static String of(EndpointAuthorizationRule rule) {
        return of(rule.method(), rule.pathPattern());
    }

    /**
     * Builds a route key from a method and path pattern.
     *
     * @param method      HTTP method
     * @param pathPattern security matcher path pattern
     * @return a stable method/path key
     */
    static String of(HttpMethod method, String pathPattern) {
        return method.name() + " " + pathPattern;
    }
}
