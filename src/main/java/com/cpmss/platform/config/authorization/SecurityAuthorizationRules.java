package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for physical access-control routes.
 *
 * <p>Security officers administer permits, vehicles, gates, and guard
 * assignments. Gate guards can create entry events, while broad entry review
 * remains with security administration.
 */
final class SecurityAuthorizationRules {

    private SecurityAuthorizationRules() {
    }

    /**
     * Returns security route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow security officers to maintain gate records.
                EndpointAuthorizationRules.crud(ApiPaths.GATES, ApiPaths.GATES_BY_ID,
                        RoleGroups.SECURITY),
                // Allow security officers to maintain vehicle records.
                EndpointAuthorizationRules.readableCrud(ApiPaths.VEHICLES,
                        ApiPaths.VEHICLES_BY_ID, RoleGroups.SECURITY, RoleGroups.SECURITY),
                List.of(
                        // Allow security officers to browse access permits.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.ACCESS_PERMITS,
                                RoleGroups.SECURITY),
                        // Allow security officers to inspect an access permit.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.ACCESS_PERMITS_BY_ID, RoleGroups.SECURITY),
                        // Allow security officers to issue access permits.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.ACCESS_PERMITS,
                                RoleGroups.SECURITY),
                        // Allow security officers to update access permit status/details.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.ACCESS_PERMITS_BY_ID, RoleGroups.SECURITY),
                        // Allow security officers to browse guard assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.GATE_GUARD_ASSIGNMENTS, RoleGroups.SECURITY),
                        // Allow security officers to inspect a guard assignment.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID, RoleGroups.SECURITY),
                        // Allow security officers to assign guards to gates.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.GATE_GUARD_ASSIGNMENTS, RoleGroups.SECURITY),
                        // Allow security officers to update guard assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID, RoleGroups.SECURITY),
                        // Allow security officers to review entry logs.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.ENTRIES,
                                RoleGroups.SECURITY),
                        // Allow security officers to inspect an entry log.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.ENTRIES_BY_ID,
                                RoleGroups.SECURITY),
                        // Allow gate guards to create entry/exit events.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.ENTRIES,
                                RoleGroups.ENTRY_WRITERS)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
