package com.cpmss.platform.config.authorization;

import com.cpmss.identity.auth.SystemRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.cpmss.platform.common.ApiPaths.ACCESS_PERMITS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.APPLICATIONS;
import static com.cpmss.platform.common.ApiPaths.APPLICATIONS_CV;
import static com.cpmss.platform.common.ApiPaths.APPLICATIONS_CV_DOWNLOAD_URL;
import static com.cpmss.platform.common.ApiPaths.APPLICATIONS_MINE;
import static com.cpmss.platform.common.ApiPaths.ASSIGNED_TASKS;
import static com.cpmss.platform.common.ApiPaths.ASSIGNED_TASKS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.ATTENDANCE;
import static com.cpmss.platform.common.ApiPaths.BANK_ACCOUNTS;
import static com.cpmss.platform.common.ApiPaths.BANK_ACCOUNTS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.ENTRIES;
import static com.cpmss.platform.common.ApiPaths.GATE_GUARD_ASSIGNMENTS;
import static com.cpmss.platform.common.ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.INTERNAL_REPORTS;
import static com.cpmss.platform.common.ApiPaths.INTERVIEWS_MINE;
import static com.cpmss.platform.common.ApiPaths.INVESTMENT_STAKES;
import static com.cpmss.platform.common.ApiPaths.KPI_RECORDS;
import static com.cpmss.platform.common.ApiPaths.KPI_SUMMARIES;
import static com.cpmss.platform.common.ApiPaths.PAYROLL;
import static com.cpmss.platform.common.ApiPaths.PERFORMANCE_REVIEWS;
import static com.cpmss.platform.common.ApiPaths.PERFORMANCE_REVIEWS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.PERSONS_BY_ID;
import static com.cpmss.platform.common.ApiPaths.DEPARTMENTS_MANAGERS;
import static com.cpmss.platform.common.ApiPaths.DEPARTMENTS_CURRENT_MANAGER;
import static com.cpmss.platform.common.ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISEE;
import static com.cpmss.platform.common.ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISOR;
import static com.cpmss.platform.common.ApiPaths.STAFF_POSITION_HISTORY_BY_PERSON;
import static com.cpmss.platform.common.ApiPaths.STAFF_PROFILES_BY_ID;
import static com.cpmss.platform.common.ApiPaths.USERS;
import static com.cpmss.platform.common.ApiPaths.USERS_ROLE;
import static com.cpmss.platform.common.ApiPaths.USERS_STATUS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies business role decisions in the endpoint authorization registry.
 *
 * <p>The tests protect important negative permissions while allowing
 * service-owned routes where resource authorization is enforced inside the
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
        EndpointAuthorizationRule permitDetail =
                rule(HttpMethod.GET, ACCESS_PERMITS_BY_ID).orElseThrow();

        assertThat(entryCreate.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(assignmentList.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(assignmentDetail.roles()).contains(role(SystemRole.GATE_GUARD));
        assertThat(permitDetail.roles()).contains(role(SystemRole.GATE_GUARD));

        assertThat(rulesContaining(SystemRole.GATE_GUARD))
                .allSatisfy(endpointRule ->
                        assertThat(isServiceOwnedRoute(endpointRule))
                                .as("GATE_GUARD route must be service-owned: %s %s",
                                        endpointRule.method(), endpointRule.pathPattern())
                                .isTrue());
    }

    /**
     * Confirms applicant access stays limited to owned profile and application routes.
     */
    @Test
    void allowsApplicantOnlyOnServiceOwnedRoutes() {
        EndpointAuthorizationRule applicationCreate =
                rule(HttpMethod.POST, APPLICATIONS).orElseThrow();
        EndpointAuthorizationRule applicationMine =
                rule(HttpMethod.GET, APPLICATIONS_MINE).orElseThrow();
        EndpointAuthorizationRule applicationCvUpload =
                rule(HttpMethod.PUT, APPLICATIONS_CV).orElseThrow();
        EndpointAuthorizationRule applicationCvDownload =
                rule(HttpMethod.GET, APPLICATIONS_CV_DOWNLOAD_URL).orElseThrow();
        EndpointAuthorizationRule interviewMine =
                rule(HttpMethod.GET, INTERVIEWS_MINE).orElseThrow();
        EndpointAuthorizationRule personRead =
                rule(HttpMethod.GET, PERSONS_BY_ID).orElseThrow();
        EndpointAuthorizationRule personUpdate =
                rule(HttpMethod.PUT, PERSONS_BY_ID).orElseThrow();

        assertThat(applicationCreate.roles()).contains(role(SystemRole.APPLICANT));
        assertThat(applicationMine.roles()).containsExactly(role(SystemRole.APPLICANT));
        assertThat(applicationCvUpload.roles()).contains(role(SystemRole.APPLICANT));
        assertThat(applicationCvDownload.roles()).contains(role(SystemRole.APPLICANT));
        assertThat(interviewMine.roles()).containsExactly(role(SystemRole.APPLICANT));
        assertThat(personRead.roles()).contains(role(SystemRole.APPLICANT));
        assertThat(personUpdate.roles()).contains(role(SystemRole.APPLICANT));
        assertThat(rulesContaining(SystemRole.APPLICANT))
                .allSatisfy(endpointRule ->
                        assertThat(isServiceOwnedRoute(endpointRule))
                                .as("APPLICANT route must be service-owned: %s %s",
                                        endpointRule.method(), endpointRule.pathPattern())
                                .isTrue());
    }

    /**
     * Confirms investor access stays limited to owned investment stake reads.
     */
    @Test
    void allowsInvestorOnlyOnServiceOwnedInvestmentRoutes() {
        EndpointAuthorizationRule investmentStakes =
                rule(HttpMethod.GET, INVESTMENT_STAKES).orElseThrow();

        assertThat(investmentStakes.roles()).contains(role(SystemRole.INVESTOR));
        assertThat(rulesContaining(SystemRole.INVESTOR))
                .allSatisfy(endpointRule ->
                        assertThat(isServiceOwnedRoute(endpointRule))
                                .as("INVESTOR route must be service-owned: %s %s",
                                        endpointRule.method(), endpointRule.pathPattern())
                                .isTrue());
    }

    /**
     * Confirms scoped account creators cannot manage user accounts broadly.
     */
    @Test
    void allowsDepartmentManagersAndAccountantsToCreateScopedUsersButNotManageUserAccounts() {
        assertThat(rule(HttpMethod.POST, USERS).orElseThrow().roles())
                .contains(role(SystemRole.DEPARTMENT_MANAGER), role(SystemRole.ACCOUNTANT));
        assertThat(rule(HttpMethod.GET, USERS).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER), role(SystemRole.ACCOUNTANT));
        assertThat(rule(HttpMethod.PUT, USERS_ROLE).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER), role(SystemRole.ACCOUNTANT));
        assertThat(rule(HttpMethod.PUT, USERS_STATUS).orElseThrow().roles())
                .doesNotContain(role(SystemRole.DEPARTMENT_MANAGER), role(SystemRole.ACCOUNTANT));
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
                || isAccessPermitDetailRead(rule)
                || isGateEntryCreate(rule)
                || isApplicantApplicationRoute(rule)
                || isInvestorStakeRead(rule)
                || isDepartmentManagerRead(rule)
                || isSupervisionRead(rule)
                || isSelfServiceRoute(rule);
    }


    private static boolean isSelfServiceRoute(EndpointAuthorizationRule rule) {
        if (rule.method() == HttpMethod.PUT) {
            return rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(PERSONS_BY_ID));
        }
        if (rule.method() != HttpMethod.GET) {
            return false;
        }
        return Set.of(
                PERSONS_BY_ID,
                STAFF_PROFILES_BY_ID,
                STAFF_POSITION_HISTORY_BY_PERSON,
                BANK_ACCOUNTS,
                BANK_ACCOUNTS_BY_ID,
                ASSIGNED_TASKS,
                ASSIGNED_TASKS_BY_ID,
                ATTENDANCE,
                PAYROLL,
                PERFORMANCE_REVIEWS,
                PERFORMANCE_REVIEWS_BY_ID,
                KPI_RECORDS,
                KPI_SUMMARIES
        ).stream()
                .map(EndpointAuthorizationRules::pathPattern)
                .anyMatch(rule.pathPattern()::equals);
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

    private static boolean isAccessPermitDetailRead(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.GET
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        ACCESS_PERMITS_BY_ID));
    }

    private static boolean isGateEntryCreate(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.POST
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(ENTRIES));
    }

    private static boolean isApplicantApplicationRoute(EndpointAuthorizationRule rule) {
        if (rule.method() == HttpMethod.POST
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(APPLICATIONS))) {
            return true;
        }
        if (rule.method() == HttpMethod.PUT
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(APPLICATIONS_CV))) {
            return true;
        }
        if (rule.method() == HttpMethod.GET) {
            return Set.of(
                    APPLICATIONS_MINE,
                    APPLICATIONS_CV_DOWNLOAD_URL,
                    INTERVIEWS_MINE
            ).stream()
                    .map(EndpointAuthorizationRules::pathPattern)
                    .anyMatch(rule.pathPattern()::equals);
        }
        return false;
    }

    private static boolean isInvestorStakeRead(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.GET
                && rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        INVESTMENT_STAKES));
    }

    private static boolean isDepartmentManagerRead(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.GET
                && (rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        DEPARTMENTS_MANAGERS))
                || rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        DEPARTMENTS_CURRENT_MANAGER)));
    }

    private static boolean isSupervisionRead(EndpointAuthorizationRule rule) {
        return rule.method() == HttpMethod.GET
                && (rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        PERSON_SUPERVISIONS_BY_SUPERVISOR))
                || rule.pathPattern().equals(EndpointAuthorizationRules.pathPattern(
                        PERSON_SUPERVISIONS_BY_SUPERVISEE)));
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
