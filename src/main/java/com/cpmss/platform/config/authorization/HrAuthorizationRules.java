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
                List.of(
                        // Staff position reads are public for the applicant portal.
                        // Allow HR to create staff position catalog records.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.STAFF_POSITIONS, RoleGroups.HR),
                        // Allow HR to update staff position catalog records.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.STAFF_POSITIONS_BY_ID, RoleGroups.HR),
                        // Allow HR to delete staff position catalog records.
                        EndpointAuthorizationRules.allow(HttpMethod.DELETE,
                                ApiPaths.STAFF_POSITIONS_BY_ID, RoleGroups.HR)
                ),
                // Allow HR to maintain staff profiles.
                List.of(
                        // Allow HR to browse all staff profiles.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.STAFF_PROFILES,
                                RoleGroups.HR),
                        // Allow staff-based users to read their own staff profile; service narrows scope.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.STAFF_PROFILES_BY_ID,
                                RoleGroups.STAFF_SELF_READERS),
                        // Allow HR to create staff profiles.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.STAFF_PROFILES,
                                RoleGroups.HR),
                        // Allow HR to update staff profiles.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.STAFF_PROFILES_BY_ID,
                                RoleGroups.HR),
                        // Allow HR to delete staff profiles.
                        EndpointAuthorizationRules.allow(HttpMethod.DELETE, ApiPaths.STAFF_PROFILES_BY_ID,
                                RoleGroups.HR)
                ),
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
                        // Allow HR to import applications and applicants to submit their own.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.APPLICATIONS,
                                RoleGroups.APPLICATION_SUBMITTERS),
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
                                RoleGroups.HR),
                        // Allow HR to assign staff position history rows.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.STAFF_POSITION_HISTORY, RoleGroups.HR),
                        // Allow HR to inspect staff position history rows.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.STAFF_POSITION_HISTORY_BY_PERSON,
                                RoleGroups.STAFF_SELF_READERS),
                        // Allow HR to record position salary history.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.STAFF_POSITIONS_SALARY_HISTORY, RoleGroups.HR),
                        // Allow HR to inspect position salary history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.STAFF_POSITIONS_SALARY_HISTORY, RoleGroups.HR)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
