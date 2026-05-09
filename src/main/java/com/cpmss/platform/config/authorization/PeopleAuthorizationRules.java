package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for people, contact identity, and HR catalog routes.
 *
 * <p>Broad person reads are limited to roles that need personal records for
 * HR or security work. Self-service person views require service-level
 * ownership checks and are not granted by these broad route rules.
 */
final class PeopleAuthorizationRules {

    private PeopleAuthorizationRules() {
    }

    /**
     * Returns people-domain route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow HR to maintain business role catalog entries.
                EndpointAuthorizationRules.crud(ApiPaths.ROLES, ApiPaths.ROLES_BY_ID,
                        RoleGroups.HR),
                // Allow HR to maintain qualification catalog entries.
                EndpointAuthorizationRules.crud(ApiPaths.QUALIFICATIONS,
                        ApiPaths.QUALIFICATIONS_BY_ID, RoleGroups.HR),
                List.of(
                        // Allow HR and security readers to browse person records.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PERSONS,
                                RoleGroups.PERSON_READERS),
                        // Allow HR and security readers to inspect a person record.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PERSONS_BY_ID,
                                RoleGroups.PERSON_READERS),
                        // Allow HR to create person records during onboarding.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PERSONS,
                                RoleGroups.HR),
                        // Allow HR and security to update person attributes they own.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.PERSONS_BY_ID,
                                RoleGroups.roles(RoleGroups.ADMIN, RoleGroups.GENERAL_MANAGER,
                                        RoleGroups.HR_OFFICER, RoleGroups.SECURITY_OFFICER)),
                        // Allow HR to remove person records where deletion is valid.
                        EndpointAuthorizationRules.allow(HttpMethod.DELETE, ApiPaths.PERSONS_BY_ID,
                                RoleGroups.HR)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
