package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for login-account management routes.
 *
 * <p>Account creation is broader than account management because department
 * managers can request or create accounts during staff onboarding, while
 * listing users and changing login role/status stays with user administrators.
 */
final class IdentityAuthorizationRules {

    private IdentityAuthorizationRules() {
    }

    /**
     * Returns identity route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow account creators to provision a login account.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.USERS,
                        RoleGroups.ACCOUNT_CREATORS),
                // Allow user administrators to browse login accounts.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.USERS,
                        RoleGroups.ACCOUNT_MANAGERS),
                // Allow user administrators to inspect a login account.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.USERS_BY_ID,
                        RoleGroups.ACCOUNT_MANAGERS),
                // Allow user administrators to change software role.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.USERS_ROLE,
                        RoleGroups.ACCOUNT_MANAGERS),
                // Allow user administrators to activate, suspend, or disable login.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.USERS_STATUS,
                        RoleGroups.ACCOUNT_MANAGERS)
        );
    }
}
