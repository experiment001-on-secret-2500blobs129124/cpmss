package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for internal report routes.
 *
 * <p>Current report routes expose broad list and mutation operations. Reporter
 * and assigned-role visibility should be granted through scoped service rules
 * before staff or team roles receive direct route access.
 */
final class CommunicationAuthorizationRules {

    private CommunicationAuthorizationRules() {
    }

    /**
     * Returns communication route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow business administrators to view unread report counts.
                EndpointAuthorizationRules.allow(HttpMethod.GET,
                        ApiPaths.INTERNAL_REPORTS_UNREAD_COUNT, RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to browse internal reports.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INTERNAL_REPORTS,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to inspect an internal report.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INTERNAL_REPORTS_BY_ID,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to file internal reports from broad route.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.INTERNAL_REPORTS,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to update an internal report.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_BY_ID,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to mark an internal report read.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_READ,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to mark an internal report unread.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_UNREAD,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow business administrators to resolve an internal report.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_RESOLVE,
                        RoleGroups.BUSINESS_ADMIN)
        );
    }
}
