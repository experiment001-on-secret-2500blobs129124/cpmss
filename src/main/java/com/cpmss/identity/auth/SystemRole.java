package com.cpmss.identity.auth;

/**
 * System-level permission roles for software access.
 *
 * <p>Independent from business roles ({@code Person_Role}).
 * See REQUIREMENTS.md § 2 and DATABASE.md § "Role Architecture".
 *
 * <p>Role hierarchy (inheritance):
 * <ul>
 *   <li>All staff roles (HR_OFFICER through GATE_GUARD) inherit STAFF permissions</li>
 *   <li>INVESTOR, APPLICANT, and ADMIN do not inherit STAFF</li>
 * </ul>
 */
public enum SystemRole {

    // --- Non-staff roles ---

    /** IT bootstrap / break-glass emergency access. Not a business role. */
    ADMIN,

    // --- Staff roles (all inherit STAFF base permissions) ---

    /** Compound owner — full business authority, sees everything. */
    GENERAL_MANAGER,

    /** HR operations — recruitment, staff, KPI, salary, performance. */
    HR_OFFICER,

    /** Finance — contracts, payments, bank accounts, pricing, payroll. */
    ACCOUNTANT,

    /** Security — permits, gates, guard assignments, vehicles, blacklist. */
    SECURITY_OFFICER,

    /** Maintenance — work orders, facilities, buildings, units, vendors. */
    FACILITY_OFFICER,

    /** Department head — own department: tasks, attendance, reviews, KPI. */
    DEPARTMENT_MANAGER,

    /** Team lead — views own supervisees' data, files internal reports. */
    SUPERVISOR,

    /** Gate operations — log entry/exit events, verify permits. */
    GATE_GUARD,

    /** Regular employee — read-only access to own records. */
    STAFF,

    // --- Non-staff login roles ---

    /** Read-only financial dashboard — investment stakes, occupancy. */
    INVESTOR,

    /** Job portal access — browse positions, submit applications. */
    APPLICANT
}
