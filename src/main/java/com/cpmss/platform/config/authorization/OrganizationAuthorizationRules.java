package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for department and organization structure routes.
 *
 * <p>Department structure controls staff placement, managers, and location
 * history, so broad mutation is kept with HR and business administration.
 */
final class OrganizationAuthorizationRules {

    private OrganizationAuthorizationRules() {
    }

    /**
     * Returns organization route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow HR to maintain department catalog records.
                EndpointAuthorizationRules.crud(ApiPaths.DEPARTMENTS,
                        ApiPaths.DEPARTMENTS_BY_ID, RoleGroups.HR),
                List.of(
                        // Allow HR to add department location history entries.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.DEPARTMENTS_LOCATION_HISTORY, RoleGroups.HR),
                        // Allow HR to inspect department location history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.DEPARTMENTS_LOCATION_HISTORY, RoleGroups.HR),
                        // Allow HR to assign department managers.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.DEPARTMENTS_MANAGERS, RoleGroups.HR),
                        // Allow HR to inspect department manager history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.DEPARTMENTS_MANAGERS, RoleGroups.HR)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
