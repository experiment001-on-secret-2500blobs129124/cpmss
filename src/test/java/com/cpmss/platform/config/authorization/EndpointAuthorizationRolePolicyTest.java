package com.cpmss.platform.config.authorization;

import com.cpmss.identity.auth.SystemRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.cpmss.platform.common.ApiPaths.ENTRIES;
import static com.cpmss.platform.common.ApiPaths.INTERNAL_REPORTS;
import static com.cpmss.platform.common.ApiPaths.USERS;
import static com.cpmss.platform.common.ApiPaths.USERS_ROLE;
import static com.cpmss.platform.common.ApiPaths.USERS_STATUS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies business role decisions in the endpoint authorization registry.
 *
 * <p>The tests protect important negative permissions as much as positive
 * grants. Broad routes should not become a shortcut around scoped service
 * ownership rules.
 */
class EndpointAuthorizationRolePolicyTest {

    /**
     * Confirms self-scoped roles do not receive broad collection/detail access.
     */
    @Test
    void keepsSelfScopedRolesOffBroadRoutes() {
        Set<String> selfScopedRoles = Set.of(
                role(SystemRole.STAFF),
                role(SystemRole.SUPERVISOR),
                role(SystemRole.APPLICANT),
                role(SystemRole.INVESTOR));

        EndpointAuthorizationRules.roleRules().forEach(rule ->
                assertThat(rule.roles()).doesNotContainAnyElementsOf(selfScopedRoles));
    }

    /**
     * Confirms gate guards can create entry events but cannot browse all logs.
     */
    @Test
    void allowsGateGuardOnlyToCreateEntryRecords() {
        EndpointAuthorizationRule entryCreate = rule(HttpMethod.POST, ENTRIES).orElseThrow();

        assertThat(entryCreate.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(rulesContaining(SystemRole.GATE_GUARD)).containsExactly(entryCreate);
    }

    /**
     * Confirms department managers can create accounts without managing users.
     */
    @Test
    void allowsDepartmentManagersToCreateUsersButNotManageUserAccounts() {
        assertThat(rule(HttpMethod.POST, USERS).orElseThrow().roles())
                .contains(role(SystemRole.DEPARTMENT_MANAGER));
        assertThat(rule(HttpMethod.GET, USERS).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER));
        assertThat(rule(HttpMethod.PUT, USERS_ROLE).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER));
        assertThat(rule(HttpMethod.PUT, USERS_STATUS).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER));
    }

    /**
     * Confirms broad internal-report routes stay restricted until scoped access exists.
     */
    @Test
    void limitsInternalReportBroadRoutesToBusinessAdministrators() {
        Set<String> businessAdministrators = Set.of(
                role(SystemRole.ADMIN),
                role(SystemRole.GENERAL_MANAGER));

        List<EndpointAuthorizationRule> reportRules =
                EndpointAuthorizationRules.roleRules()
                        .stream()
                        .filter(rule -> rule.pathPattern().startsWith(INTERNAL_REPORTS))
                        .toList();

        assertThat(reportRules).hasSize(8);
        reportRules.forEach(rule ->
                assertThat(rule.roles()).containsExactlyInAnyOrderElementsOf(businessAdministrators));
    }

    private static Optional<EndpointAuthorizationRule> rule(HttpMethod method, String apiPath) {
        String pattern = EndpointAuthorizationRules.pathPattern(apiPath);
        return EndpointAuthorizationRules.roleRules()
                .stream()
                .filter(rule -> rule.method() == method && rule.pathPattern().equals(pattern))
                .findFirst();
    }

    private static List<EndpointAuthorizationRule> rulesContaining(SystemRole role) {
        String roleName = role(role);
        return EndpointAuthorizationRules.roleRules()
                .stream()
                .filter(rule -> rule.roles().contains(roleName))
                .toList();
    }

    private static String role(SystemRole role) {
        return role.name();
    }
}
