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
}
