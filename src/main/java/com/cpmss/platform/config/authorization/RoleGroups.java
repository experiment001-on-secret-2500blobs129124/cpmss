package com.cpmss.platform.config.authorization;

import com.cpmss.identity.auth.SystemRole;

import java.util.List;

/**
 * Shared role groups used by endpoint authorization policies.
 *
 * <p>The groups express business authority, not Java package ownership.
 * Role strings intentionally omit the {@code ROLE_} prefix because Spring
 * Security adds it when evaluating {@code hasAnyRole}.
 *
 * @see SystemRole
 */
final class RoleGroups {

    static final String ADMIN = role(SystemRole.ADMIN);
    static final String GENERAL_MANAGER = role(SystemRole.GENERAL_MANAGER);
    static final String HR_OFFICER = role(SystemRole.HR_OFFICER);
    static final String ACCOUNTANT = role(SystemRole.ACCOUNTANT);
    static final String SECURITY_OFFICER = role(SystemRole.SECURITY_OFFICER);
    static final String FACILITY_OFFICER = role(SystemRole.FACILITY_OFFICER);
    static final String DEPARTMENT_MANAGER = role(SystemRole.DEPARTMENT_MANAGER);
    static final String SUPERVISOR = role(SystemRole.SUPERVISOR);
    static final String GATE_GUARD = role(SystemRole.GATE_GUARD);
    static final String STAFF = role(SystemRole.STAFF);
    static final String INVESTOR = role(SystemRole.INVESTOR);
    static final String APPLICANT = role(SystemRole.APPLICANT);

    /** Roles allowed to create login accounts for people they onboard. */
    static final List<String> ACCOUNT_CREATORS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, DEPARTMENT_MANAGER);

    /** Roles allowed to list users and change login role/status metadata. */
    static final List<String> ACCOUNT_MANAGERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER);

    /** Roles with unrestricted business oversight across operational data. */
    static final List<String> BUSINESS_ADMIN = roles(ADMIN, GENERAL_MANAGER);

    /** Roles that administer staff, hiring, role catalogs, and HR setup. */
    static final List<String> HR = roles(ADMIN, GENERAL_MANAGER, HR_OFFICER);

    /** Roles allowed to submit job applications at route level. */
    static final List<String> APPLICATION_SUBMITTERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, APPLICANT);

    /** Roles that administer contracts, payments, bank accounts, and payroll money. */
    static final List<String> FINANCE = roles(ADMIN, GENERAL_MANAGER, ACCOUNTANT);

    /** Roles that administer access permits, gates, vehicles, and guard posts. */
    static final List<String> SECURITY = roles(ADMIN, GENERAL_MANAGER, SECURITY_OFFICER);

    /** Roles that administer facilities, units, buildings, vendors, and work orders. */
    static final List<String> FACILITY = roles(ADMIN, GENERAL_MANAGER, FACILITY_OFFICER);

    /** Roles that can operate staff KPI and review endpoints at broad route level. */
    static final List<String> PERFORMANCE = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, DEPARTMENT_MANAGER);

    /** Roles that operate department tasks, assignments, and attendance routes. */
    static final List<String> DEPARTMENT_OPERATIONS = roles(
            ADMIN, GENERAL_MANAGER, DEPARTMENT_MANAGER);

    /** Roles allowed to read service-filtered department manager assignments. */
    static final List<String> ORGANIZATION_SCOPE_READERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, DEPARTMENT_MANAGER);

    /** Roles allowed to read service-filtered supervision relationships. */
    static final List<String> SUPERVISION_READERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, DEPARTMENT_MANAGER, SUPERVISOR);

    /** Roles allowed to browse broad person record lists. */
    static final List<String> PERSON_READERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, SECURITY_OFFICER);

    /** Roles allowed to read facility/property records used in finance operations. */
    static final List<String> FACILITY_READERS = roles(
            ADMIN, GENERAL_MANAGER, FACILITY_OFFICER, ACCOUNTANT);

    /** Roles allowed to read individual access permits at a gate checkpoint. */
    static final List<String> ACCESS_PERMIT_READERS = roles(
            ADMIN, GENERAL_MANAGER, SECURITY_OFFICER, GATE_GUARD);

    /** Roles allowed to read gate assignments before service ownership narrows rows. */
    static final List<String> GATE_GUARD_ASSIGNMENT_READERS = roles(
            ADMIN, GENERAL_MANAGER, SECURITY_OFFICER, GATE_GUARD);

    /** Roles allowed to create gate entry events at route level. */
    static final List<String> ENTRY_WRITERS = roles(
            ADMIN, GENERAL_MANAGER, SECURITY_OFFICER, GATE_GUARD);

    /** Staff-based roles allowed to enter service-filtered self/owned read routes. */
    static final List<String> STAFF_SELF_READERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, ACCOUNTANT, SECURITY_OFFICER,
            FACILITY_OFFICER, DEPARTMENT_MANAGER, SUPERVISOR, GATE_GUARD, STAFF);

    /** Roles allowed to enter service-filtered own person profile routes. */
    static final List<String> PERSON_SELF_READERS = roles(
            ADMIN, GENERAL_MANAGER, HR_OFFICER, ACCOUNTANT, SECURITY_OFFICER,
            FACILITY_OFFICER, DEPARTMENT_MANAGER, SUPERVISOR, GATE_GUARD, STAFF,
            INVESTOR, APPLICANT);

    /** Internal staff roles that can file and participate in internal reports. */
    static final List<String> INTERNAL_REPORT_USERS = STAFF_SELF_READERS;

    private RoleGroups() {
    }

    /**
     * Builds an immutable role group from raw system role names.
     *
     * @param roles role names without the {@code ROLE_} prefix
     * @return immutable role group
     */
    static List<String> roles(String... roles) {
        return List.of(roles);
    }

    private static String role(SystemRole role) {
        return role.name();
    }
}
