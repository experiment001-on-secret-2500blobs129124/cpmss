package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Central registry of REST endpoint role rules.
 *
 * <p>The registry keeps path-level authorization aligned with the system role
 * model while letting each bounded context own its route list. Per-record
 * ownership checks stay in services because they need authenticated user
 * identity plus repository lookups.
 */
public final class EndpointAuthorizationRules {

    private EndpointAuthorizationRules() {
    }

    /**
     * Returns role checks for all non-public REST endpoints.
     *
     * <p>Rules are grouped by bounded context and flattened in a stable order
     * before Spring Security consumes them.
     *
     * @return immutable endpoint authorization rules
     */
    public static List<EndpointAuthorizationRule> roleRules() {
        return Stream.of(
                IdentityAuthorizationRules.rules(),
                PeopleAuthorizationRules.rules(),
                OrganizationAuthorizationRules.rules(),
                PropertyAuthorizationRules.rules(),
                SecurityAuthorizationRules.rules(),
                LeasingAuthorizationRules.rules(),
                FinanceAuthorizationRules.rules(),
                HrAuthorizationRules.rules(),
                WorkforceAuthorizationRules.rules(),
                PerformanceAuthorizationRules.rules(),
                MaintenanceAuthorizationRules.rules(),
                CommunicationAuthorizationRules.rules()
            )
            .flatMap(List::stream)
            .toList();
    }

    /**
     * Converts an {@link ApiPaths} MVC variable path into a security matcher.
     *
     * <p>The project stores path variables as {@code {id}} in {@link ApiPaths};
     * Spring Security matchers use a wildcard segment for the same route shape.
     *
     * @param apiPath the REST path constant
     * @return a path pattern safe for Spring Security request matching
     */
    public static String pathPattern(String apiPath) {
        Objects.requireNonNull(apiPath, "apiPath must not be null");
        return apiPath.replaceAll("\\{[^/]+}", "*");
    }

    /**
     * Creates a single endpoint authorization rule.
     *
     * @param method       protected HTTP method
     * @param apiPath      controller route path
     * @param allowedRoles roles allowed to call the route
     * @return endpoint authorization rule using a security matcher path
     */
    static EndpointAuthorizationRule allow(HttpMethod method, String apiPath,
                                           List<String> allowedRoles) {
        return new EndpointAuthorizationRule(method, pathPattern(apiPath), allowedRoles);
    }

    /**
     * Creates same-role CRUD rules for a collection and item route pair.
     *
     * @param collection   collection route path
     * @param item         item route path
     * @param allowedRoles roles allowed to read and write the resource
     * @return immutable CRUD endpoint rules
     */
    static List<EndpointAuthorizationRule> crud(String collection, String item,
                                                List<String> allowedRoles) {
        return readableCrud(collection, item, allowedRoles, allowedRoles);
    }

    /**
     * Creates CRUD rules where read and write roles differ.
     *
     * @param collection collection route path
     * @param item       item route path
     * @param writers    roles allowed to create, update, and delete
     * @param readers    roles allowed to list and retrieve
     * @return immutable CRUD endpoint rules
     */
    static List<EndpointAuthorizationRule> readableCrud(String collection, String item,
                                                        List<String> writers,
                                                        List<String> readers) {
        return List.of(
                allow(HttpMethod.GET, collection, readers),
                allow(HttpMethod.GET, item, readers),
                allow(HttpMethod.POST, collection, writers),
                allow(HttpMethod.PUT, item, writers),
                allow(HttpMethod.DELETE, item, writers)
        );
    }
}
