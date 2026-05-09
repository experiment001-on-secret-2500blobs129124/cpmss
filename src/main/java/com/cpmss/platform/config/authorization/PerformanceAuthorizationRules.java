package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for KPI policy, KPI records, summaries, and performance reviews.
 *
 * <p>HR owns policy setup. Department managers can operate KPI recording and
 * review routes, with per-department scope enforced later in service rules.
 */
final class PerformanceAuthorizationRules {

    private PerformanceAuthorizationRules() {
    }

    /**
     * Returns performance route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow HR to browse KPI policies.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.KPI_POLICIES,
                        RoleGroups.HR),
                // Allow HR to inspect a KPI policy.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.KPI_POLICIES_BY_ID,
                        RoleGroups.HR),
                // Allow HR to create KPI policies.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.KPI_POLICIES,
                        RoleGroups.HR),
                // Allow HR to update KPI policies.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.KPI_POLICIES_BY_ID,
                        RoleGroups.HR),
                // Allow HR and department managers to browse performance reviews.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PERFORMANCE_REVIEWS,
                        RoleGroups.PERFORMANCE),
                // Allow HR and department managers to inspect a performance review.
                EndpointAuthorizationRules.allow(HttpMethod.GET,
                        ApiPaths.PERFORMANCE_REVIEWS_BY_ID, RoleGroups.PERFORMANCE),
                // Allow HR and department managers to create performance reviews.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PERFORMANCE_REVIEWS,
                        RoleGroups.PERFORMANCE),
                // Allow HR and department managers to update performance reviews.
                EndpointAuthorizationRules.allow(HttpMethod.PUT,
                        ApiPaths.PERFORMANCE_REVIEWS_BY_ID, RoleGroups.PERFORMANCE),
                // Allow department operations roles to record daily KPI values.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.KPI_RECORDS,
                        RoleGroups.DEPARTMENT_OPERATIONS),
                // Allow HR and department managers to browse KPI records.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.KPI_RECORDS,
                        RoleGroups.PERFORMANCE),
                // Allow HR and department managers to close monthly KPI summaries.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.KPI_CLOSE,
                        RoleGroups.PERFORMANCE),
                // Allow HR and department managers to inspect KPI summaries.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.KPI_SUMMARIES,
                        RoleGroups.PERFORMANCE)
        );
    }
}
