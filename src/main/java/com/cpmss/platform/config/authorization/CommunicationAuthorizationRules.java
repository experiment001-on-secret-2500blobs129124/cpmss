package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for internal report routes.
 *
 * <p>Internal staff roles can reach report endpoints so reporters can see their
 * own reports and assigned roles can work their inbox. Service-level ownership
 * rules narrow each request to the reporter, assigned role, or business admin.
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
                // Allow internal report users to view unread report counts;
                // service rules restrict the requested role.
                EndpointAuthorizationRules.allow(HttpMethod.GET,
                        ApiPaths.INTERNAL_REPORTS_UNREAD_COUNT,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow internal report users to browse reports;
                // service rules restrict rows to own reports or assigned role.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INTERNAL_REPORTS,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow internal report users to inspect reports they own or receive.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INTERNAL_REPORTS_BY_ID,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow internal report users to file reports as themselves.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.INTERNAL_REPORTS,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow assigned report handlers to update their report queue.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_BY_ID,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow assigned report handlers to mark reports read.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_READ,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow assigned report handlers to mark reports unread.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_UNREAD,
                        RoleGroups.INTERNAL_REPORT_USERS),
                // Allow assigned report handlers to resolve reports.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_RESOLVE,
                        RoleGroups.INTERNAL_REPORT_USERS)
        );
    }
}
