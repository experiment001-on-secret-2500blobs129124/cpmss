package com.cpmss.platform.config.authorization;

import com.cpmss.identity.auth.SystemRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.cpmss.platform.common.ApiPaths.ENTRIES;
import static com.cpmss.platform.common.ApiPaths.GATE_GUARD_ASSIGNMENTS;
import static com.cpmss.platform.common.ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.INTERNAL_REPORTS;
import static com.cpmss.platform.common.ApiPaths.USERS;
import static com.cpmss.platform.common.ApiPaths.USERS_ROLE;
import static com.cpmss.platform.common.ApiPaths.USERS_STATUS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies business role decisions in the endpoint authorization registry.
 *
 * <p>The tests protect important negative permissions while allowing service-owned routes where resource authorization is enforced inside the
 * owning bounded-context service.
 */
class EndpointAuthorizationRolePolicyTest {

    /**
     * Confirms self-scoped roles do not receive unowned broad access.
     *
     * <p>Some broad-looking routes are allowed when the service narrows
     * rows or records by current-user ownership.
     */
    @Test
    void keepsSelfScopedRolesOffUnownedBroadRoutes() {
        Set<String> selfScopedRoles = Set.of(
                role(SystemRole.STAFF),
                role(SystemRole.SUPERVISOR),
                role(SystemRole.APPLICANT),
                role(SystemRole.INVESTOR),
                role(SystemRole.GATE_GUARD));

        EndpointAuthorizationRules.roleRules()
                .stream()
                .filter(rule -> !isServiceOwnedRoute(rule))
                .forEach(rule ->
                        assertThat(rule.roles()).doesNotContainAnyElementsOf(selfScopedRoles));
    }

    /**
     * Confirms gate guards can create entry events and read owned assignments,
     * but cannot browse unrelated security administration routes.
     */
    @Test
    void allowsGateGuardOnlyOnServiceOwnedRoutes() {
        EndpointAuthorizationRule entryCreate = rule(HttpMethod.POST, ENTRIES).orElseThrow();
        EndpointAuthorizationRule assignmentList =
                rule(HttpMethod.GET, GATE_GUARD_ASSIGNMENTS).orElseThrow();
        EndpointAuthorizationRule assignmentDetail =
                rule(HttpMethod.GET, GATE_GUARD_ASSIGNMENTS_BY_ID).orElseThrow();

        assertThat(entryCreate.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(assignmentList.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(assignmentDetail.roles()).contains(role(SystemRole.GATE_GUARD));

        assertThat(rulesContaining(SystemRole.GATE_GUARD))
                .allSatisfy(endpointRule ->
                        assertThat(isServiceOwnedRoute(endpointRule))
                                .as("GATE_GUARD route must be service-owned: %s %s",
                                        endpointRule.method(), endpointRule.pathPattern())
                                .isTrue());
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
     * Confirms internal-report routes are open to report participants only
     * because InternalReportService now enforces reporter, assigned-role, and
     * business-admin ownership.
     */
    @Test
    void opensInternalReportRoutesToServiceScopedInternalUsers() {
        List<EndpointAuthorizationRule> reportRules =
                EndpointAuthorizationRules.roleRules()
                        .stream()
                        .filter(EndpointAuthorizationRolePolicyTest::isInternalReportRoute)
                        .toList();

        assertThat(reportRules).hasSize(8);
        reportRules.forEach(rule ->
                assertThat(rule.roles())
                        .containsExactlyInAnyOrderElementsOf(RoleGroups.INTERNAL_REPORT_USERS));
    }

    private static boolean isServiceOwnedRoute(EndpointAuthorizationRule rule) {
        return isInternalReportRoute(rule)
                || isGateGuardAssignmentRead(rule)
                || isGateEntryCreate(rule);
    }

    private static boolean isInternalReportRoute(EndpointAuthorizationRule rule) {
        return rule.pathPattern().startsWith(INTERNAL_REPORTS);
    }

    private static boolean isGateGuardAssignmentRead(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.GET
                && (rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        GATE_GUARD_ASSIGNMENTS))
                || rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        GATE_GUARD_ASSIGNMENTS_BY_ID)));
    }

    private static boolean isGateEntryCreate(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.POST
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(ENTRIES));
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
