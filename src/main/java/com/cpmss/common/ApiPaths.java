package com.cpmss.common;

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

    // Phase 2 — Catalogs
    public static final String DEPARTMENTS = "/api/v1/departments";
    public static final String DEPARTMENTS_BY_ID = "/api/v1/departments/{id}";
    public static final String ROLES = "/api/v1/roles";
    public static final String ROLES_BY_ID = "/api/v1/roles/{id}";
    public static final String QUALIFICATIONS = "/api/v1/qualifications";
    public static final String QUALIFICATIONS_BY_ID = "/api/v1/qualifications/{id}";
    public static final String SHIFT_ATTENDANCE_TYPES = "/api/v1/shift-attendance-types";
    public static final String SHIFT_ATTENDANCE_TYPES_BY_ID = "/api/v1/shift-attendance-types/{id}";
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
    public static final String FACILITIES = "/api/v1/facilities";
    public static final String FACILITIES_BY_ID = "/api/v1/facilities/{id}";
    public static final String GATES = "/api/v1/gates";
    public static final String GATES_BY_ID = "/api/v1/gates/{id}";
    public static final String VEHICLES = "/api/v1/vehicles";
    public static final String VEHICLES_BY_ID = "/api/v1/vehicles/{id}";
}
