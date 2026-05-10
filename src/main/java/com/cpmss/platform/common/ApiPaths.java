package com.cpmss.platform.common;

/**
 * Central registry of all REST API path constants.
 *
 * <p>Controllers import these constants — strings are never hardcoded
 * in controller classes. Constants use SCREAMING_SNAKE_CASE.
 */
public final class ApiPaths {

    private ApiPaths() {}

    // Phase 1 — Auth
    public static final String SETUP = "/setup";
    public static final String AUTH_LOGIN = "/api/v1/auth/login";
    public static final String AUTH_REFRESH = "/api/v1/auth/refresh";

    // Phase A — User Management
    public static final String USERS = "/api/v1/users";
    public static final String USERS_BY_ID = "/api/v1/users/{id}";
    public static final String USERS_ROLE = "/api/v1/users/{id}/role";
    public static final String USERS_STATUS = "/api/v1/users/{id}/status";
    public static final String REGISTER = "/register";
    public static final String REGISTER_APPLICANT = "/register/applicant";

    // Phase 2 — Catalogs
    public static final String DEPARTMENTS = "/api/v1/departments";
    public static final String DEPARTMENTS_BY_ID = "/api/v1/departments/{id}";
    public static final String DEPARTMENTS_LOCATION_HISTORY = "/api/v1/departments/{id}/location-history";
    public static final String DEPARTMENTS_MANAGERS = "/api/v1/departments/{id}/managers";
    public static final String DEPARTMENTS_CURRENT_MANAGER = "/api/v1/departments/{id}/managers/current";
    public static final String PERSON_SUPERVISIONS = "/api/v1/person-supervisions";
    public static final String PERSON_SUPERVISIONS_END = "/api/v1/person-supervisions/end";
    public static final String PERSON_SUPERVISIONS_BY_SUPERVISOR =
            "/api/v1/person-supervisions/supervisors/{supervisorId}";
    public static final String PERSON_SUPERVISIONS_BY_SUPERVISEE =
            "/api/v1/person-supervisions/supervisees/{superviseeId}";
    public static final String ROLES = "/api/v1/roles";
    public static final String ROLES_BY_ID = "/api/v1/roles/{id}";
    public static final String QUALIFICATIONS = "/api/v1/qualifications";
    public static final String QUALIFICATIONS_BY_ID = "/api/v1/qualifications/{id}";
    public static final String SHIFT_ATTENDANCE_TYPES = "/api/v1/shift-attendance-types";
    public static final String SHIFT_ATTENDANCE_TYPES_BY_ID = "/api/v1/shift-attendance-types/{id}";
    public static final String SHIFT_ATTENDANCE_TYPES_LAWS = "/api/v1/shift-attendance-types/{id}/laws";
    public static final String SHIFT_ATTENDANCE_TYPES_CURRENT_LAW =
            "/api/v1/shift-attendance-types/{id}/laws/current";
    public static final String TASKS = "/api/v1/tasks";
    public static final String TASKS_BY_ID = "/api/v1/tasks/{id}";

    // Phase 3 — Core Entities
    public static final String COMPOUNDS = "/api/v1/compounds";
    public static final String COMPOUNDS_BY_ID = "/api/v1/compounds/{id}";
    public static final String COMPANIES = "/api/v1/companies";
    public static final String COMPANIES_BY_ID = "/api/v1/companies/{id}";
    public static final String PERSONS = "/api/v1/persons";
    public static final String PERSONS_BY_ID = "/api/v1/persons/{id}";
    public static final String BUILDINGS = "/api/v1/buildings";
    public static final String BUILDINGS_BY_ID = "/api/v1/buildings/{id}";
    public static final String UNITS = "/api/v1/units";
    public static final String UNITS_BY_ID = "/api/v1/units/{id}";
    public static final String UNITS_PRICING_HISTORY = "/api/v1/units/{id}/pricing-history";
    public static final String UNITS_STATUS_HISTORY = "/api/v1/units/{id}/status-history";
    public static final String FACILITIES = "/api/v1/facilities";
    public static final String FACILITIES_BY_ID = "/api/v1/facilities/{id}";
    public static final String FACILITIES_HOURS_HISTORY = "/api/v1/facilities/{id}/hours-history";
    public static final String FACILITIES_MANAGERS = "/api/v1/facilities/{id}/managers";
    public static final String FACILITIES_CURRENT_MANAGER = "/api/v1/facilities/{id}/managers/current";
    public static final String GATES = "/api/v1/gates";
    public static final String GATES_BY_ID = "/api/v1/gates/{id}";
    public static final String VEHICLES = "/api/v1/vehicles";
    public static final String VEHICLES_BY_ID = "/api/v1/vehicles/{id}";
    public static final String VEHICLE_PERMIT = "/api/v1/vehicles/{id}/permits/{permitId}";

    // Phase 4 — Staff & HR
    public static final String STAFF_POSITIONS = "/api/v1/staff-positions";
    public static final String STAFF_POSITIONS_BY_ID = "/api/v1/staff-positions/{id}";
    public static final String STAFF_POSITIONS_SALARY_HISTORY =
            "/api/v1/staff-positions/{id}/salary-history";
    public static final String STAFF_POSITION_HISTORY = "/api/v1/staff-position-history";
    public static final String STAFF_POSITION_HISTORY_BY_PERSON =
            "/api/v1/staff-position-history/persons/{personId}";
    public static final String STAFF_PROFILES = "/api/v1/staff-profiles";
    public static final String STAFF_PROFILES_BY_ID = "/api/v1/staff-profiles/{id}";
    public static final String BANK_ACCOUNTS = "/api/v1/bank-accounts";
    public static final String BANK_ACCOUNTS_BY_ID = "/api/v1/bank-accounts/{id}";

    // Phase 5 — Contracts & Occupancy
    public static final String CONTRACTS = "/api/v1/contracts";
    public static final String CONTRACTS_BY_ID = "/api/v1/contracts/{id}";
    public static final String CONTRACTS_STATUS = "/api/v1/contracts/{id}/status";
    public static final String CONTRACT_PARTIES = "/api/v1/contracts/{id}/parties";
    public static final String CONTRACT_RESIDENTS = "/api/v1/contracts/{id}/residents";
    public static final String INSTALLMENTS = "/api/v1/installments";
    public static final String INSTALLMENTS_BY_ID = "/api/v1/installments/{id}";

    // Phase 6 — Access Control
    public static final String ACCESS_PERMITS = "/api/v1/access-permits";
    public static final String ACCESS_PERMITS_BY_ID = "/api/v1/access-permits/{id}";
    public static final String ENTRIES = "/api/v1/entries";
    public static final String ENTRIES_BY_ID = "/api/v1/entries/{id}";

    // Phase 7 — Recruitment
    public static final String APPLICATIONS = "/api/v1/applications";

    // Phase 8 — Attendance & Payroll
    public static final String ASSIGNED_TASKS = "/api/v1/assigned-tasks";
    public static final String ASSIGNED_TASKS_BY_ID = "/api/v1/assigned-tasks/{id}";
    public static final String KPI_POLICIES = "/api/v1/kpi-policies";
    public static final String KPI_POLICIES_BY_ID = "/api/v1/kpi-policies/{id}";
    public static final String PERFORMANCE_REVIEWS = "/api/v1/performance-reviews";
    public static final String PERFORMANCE_REVIEWS_BY_ID = "/api/v1/performance-reviews/{id}";
    public static final String GATE_GUARD_ASSIGNMENTS = "/api/v1/gate-guard-assignments";
    public static final String GATE_GUARD_ASSIGNMENTS_BY_ID = "/api/v1/gate-guard-assignments/{id}";

    // Phase 9 — Work Orders
    public static final String WORK_ORDERS = "/api/v1/work-orders";
    public static final String WORK_ORDERS_BY_ID = "/api/v1/work-orders/{id}";
    public static final String WORK_ORDERS_ASSIGNMENTS = "/api/v1/work-orders/{id}/assignments";

    // Phase 10 — Payments
    public static final String PAYMENTS = "/api/v1/payments";
    public static final String PAYMENTS_BY_ID = "/api/v1/payments/{id}";

    // Internal Reports
    public static final String INTERNAL_REPORTS = "/api/v1/internal-reports";
    public static final String INTERNAL_REPORTS_BY_ID = "/api/v1/internal-reports/{id}";
    public static final String INTERNAL_REPORTS_READ = "/api/v1/internal-reports/{id}/read";
    public static final String INTERNAL_REPORTS_UNREAD = "/api/v1/internal-reports/{id}/unread";
    public static final String INTERNAL_REPORTS_RESOLVE = "/api/v1/internal-reports/{id}/resolve";
    public static final String INTERNAL_REPORTS_UNREAD_COUNT = "/api/v1/internal-reports/unread/count";

    // Recruitment / Hiring Pipeline
    public static final String INTERVIEWS = "/api/v1/interviews";
    public static final String INTERVIEWS_RESULT = "/api/v1/interviews/result";
    public static final String HIRE_AGREEMENTS = "/api/v1/hire-agreements";

    // Payroll / Attendance / Salary
    public static final String ATTENDANCE = "/api/v1/attendance";
    public static final String PAYROLL_CLOSE = "/api/v1/payroll/close";
    public static final String PAYROLL = "/api/v1/payroll";
    public static final String STAFF_SALARY = "/api/v1/staff-salary";

    // KPI Scoring
    public static final String KPI_RECORDS = "/api/v1/kpi-records";
    public static final String KPI_CLOSE = "/api/v1/kpi/close";
    public static final String KPI_SUMMARIES = "/api/v1/kpi-summaries";

    // Polymorphic Payments
    public static final String PAYMENTS_INSTALLMENT = "/api/v1/payments/installment";
    public static final String PAYMENTS_WORK_ORDER = "/api/v1/payments/work-order";
    public static final String PAYMENTS_PAYROLL = "/api/v1/payments/payroll";
}
