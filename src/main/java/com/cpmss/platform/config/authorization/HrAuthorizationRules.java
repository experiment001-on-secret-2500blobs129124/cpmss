package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for hiring, staff profile, position, and salary setup routes.
 *
 * <p>HR owns broad staff lifecycle setup. Department-specific staff access
 * belongs to scoped service rules, not unrestricted route grants.
 */
final class HrAuthorizationRules {

    private HrAuthorizationRules() {
    }

    /**
     * Returns HR route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow HR to maintain staff position catalog records.
                EndpointAuthorizationRules.crud(ApiPaths.STAFF_POSITIONS,
                        ApiPaths.STAFF_POSITIONS_BY_ID, RoleGroups.HR),
                // Allow HR to maintain staff profiles.
                EndpointAuthorizationRules.crud(ApiPaths.STAFF_PROFILES,
                        ApiPaths.STAFF_PROFILES_BY_ID, RoleGroups.HR),
                // Allow HR to maintain shift attendance type catalog records.
                EndpointAuthorizationRules.crud(ApiPaths.SHIFT_ATTENDANCE_TYPES,
                        ApiPaths.SHIFT_ATTENDANCE_TYPES_BY_ID, RoleGroups.HR),
                List.of(
                        // Allow HR to browse job applications.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.APPLICATIONS,
                                RoleGroups.HR),
                        // Allow HR to add shift attendance laws.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS, RoleGroups.HR),
                        // Allow HR to inspect shift attendance laws.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS, RoleGroups.HR),
                        // Allow HR to inspect the current shift attendance law.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.SHIFT_ATTENDANCE_TYPES_CURRENT_LAW, RoleGroups.HR),
                        // Allow HR to submit or import job applications.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.APPLICATIONS,
                                RoleGroups.HR),
                        // Allow HR to schedule interviews.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.INTERVIEWS,
                                RoleGroups.HR),
                        // Allow HR to record interview outcomes.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.INTERVIEWS_RESULT, RoleGroups.HR),
                        // Allow HR to create hire agreements.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.HIRE_AGREEMENTS, RoleGroups.HR),
                        // Allow HR to record staff salary history.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.STAFF_SALARY,
                                RoleGroups.HR)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
