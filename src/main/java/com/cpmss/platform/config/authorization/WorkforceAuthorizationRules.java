package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for tasks, assignments, attendance, and payroll routes.
 *
 * <p>Department managers operate task and attendance routes at broad path
 * level. Per-department enforcement still belongs in service rules because it
 * requires staff and department relationships.
 */
final class WorkforceAuthorizationRules {

    private WorkforceAuthorizationRules() {
    }

    /**
     * Returns workforce route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow department operations roles to maintain task catalog records.
                EndpointAuthorizationRules.crud(ApiPaths.TASKS, ApiPaths.TASKS_BY_ID,
                        RoleGroups.DEPARTMENT_OPERATIONS),
                List.of(
                        // Allow staff-based users to browse service-filtered task assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.ASSIGNED_TASKS,
                                RoleGroups.STAFF_SELF_READERS),
                        // Allow staff-based users to inspect service-filtered task assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.ASSIGNED_TASKS_BY_ID,
                                RoleGroups.STAFF_SELF_READERS),
                        // Allow department operations roles to assign staff to tasks.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.ASSIGNED_TASKS,
                                RoleGroups.DEPARTMENT_OPERATIONS),
                        // Allow department operations roles to update task assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.ASSIGNED_TASKS_BY_ID,
                                RoleGroups.DEPARTMENT_OPERATIONS),
                        // Allow department operations roles to record attendance.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.ATTENDANCE,
                                RoleGroups.DEPARTMENT_OPERATIONS),
                        // Allow HR and department operations roles to review attendance.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.ATTENDANCE,
                                RoleGroups.STAFF_SELF_READERS),
                        // Allow finance to close monthly payroll.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PAYROLL_CLOSE,
                                RoleGroups.FINANCE),
                        // Allow finance to inspect payroll snapshots.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PAYROLL,
                                RoleGroups.STAFF_SELF_READERS)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
