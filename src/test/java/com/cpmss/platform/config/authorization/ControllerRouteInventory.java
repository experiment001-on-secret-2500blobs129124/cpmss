package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Test inventory of controller routes that require role authorization.
 *
 * <p>The inventory mirrors current controller mappings so coverage tests can
 * detect a route that was added without an explicit endpoint role rule.
 */
final class ControllerRouteInventory {

    private ControllerRouteInventory() {
    }

    /**
     * Returns all non-public controller routes that should be role protected.
     *
     * @return method/path keys for protected routes
     */
    static Set<String> protectedRoutes() {
        LinkedHashSet<String> routes = new LinkedHashSet<>();

        addUsers(routes);
        addHrCatalogs(routes);
        addPeople(routes);
        addProperty(routes);
        addSecurity(routes);
        addLeasing(routes);
        addFinance(routes);
        addRecruitment(routes);
        addWorkforce(routes);
        addPerformance(routes);
        addMaintenance(routes);
        addCommunication(routes);

        return Set.copyOf(routes);
    }

    private static void addUsers(Set<String> routes) {
        add(routes, HttpMethod.POST, ApiPaths.USERS);
        add(routes, HttpMethod.GET, ApiPaths.USERS);
        add(routes, HttpMethod.GET, ApiPaths.USERS_BY_ID);
        add(routes, HttpMethod.PUT, ApiPaths.USERS_ROLE);
        add(routes, HttpMethod.PUT, ApiPaths.USERS_STATUS);
    }

    private static void addHrCatalogs(Set<String> routes) {
        crud(routes, ApiPaths.ROLES, ApiPaths.ROLES_BY_ID);
        crud(routes, ApiPaths.QUALIFICATIONS, ApiPaths.QUALIFICATIONS_BY_ID);
        crud(routes, ApiPaths.DEPARTMENTS, ApiPaths.DEPARTMENTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.DEPARTMENTS_LOCATION_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.DEPARTMENTS_LOCATION_HISTORY);
        add(routes, HttpMethod.POST, ApiPaths.DEPARTMENTS_MANAGERS);
        add(routes, HttpMethod.GET, ApiPaths.DEPARTMENTS_MANAGERS);
        add(routes, HttpMethod.GET, ApiPaths.DEPARTMENTS_CURRENT_MANAGER);
        add(routes, HttpMethod.POST, ApiPaths.PERSON_SUPERVISIONS);
        add(routes, HttpMethod.PUT, ApiPaths.PERSON_SUPERVISIONS_END);
        add(routes, HttpMethod.GET, ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISOR);
        add(routes, HttpMethod.GET, ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISEE);
        crud(routes, ApiPaths.STAFF_POSITIONS, ApiPaths.STAFF_POSITIONS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.STAFF_POSITION_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.STAFF_POSITION_HISTORY_BY_PERSON);
        add(routes, HttpMethod.POST, ApiPaths.STAFF_POSITIONS_SALARY_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.STAFF_POSITIONS_SALARY_HISTORY);
        crud(routes, ApiPaths.SHIFT_ATTENDANCE_TYPES, ApiPaths.SHIFT_ATTENDANCE_TYPES_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS);
        add(routes, HttpMethod.GET, ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS);
        add(routes, HttpMethod.GET, ApiPaths.SHIFT_ATTENDANCE_TYPES_CURRENT_LAW);
        crud(routes, ApiPaths.TASKS, ApiPaths.TASKS_BY_ID);
    }

    private static void addPeople(Set<String> routes) {
        crud(routes, ApiPaths.PERSONS, ApiPaths.PERSONS_BY_ID);
        crud(routes, ApiPaths.STAFF_PROFILES, ApiPaths.STAFF_PROFILES_BY_ID);
    }

    private static void addProperty(Set<String> routes) {
        crud(routes, ApiPaths.COMPOUNDS, ApiPaths.COMPOUNDS_BY_ID);
        crud(routes, ApiPaths.BUILDINGS, ApiPaths.BUILDINGS_BY_ID);
        crud(routes, ApiPaths.UNITS, ApiPaths.UNITS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.UNITS_PRICING_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.UNITS_PRICING_HISTORY);
        add(routes, HttpMethod.POST, ApiPaths.UNITS_STATUS_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.UNITS_STATUS_HISTORY);
        crud(routes, ApiPaths.FACILITIES, ApiPaths.FACILITIES_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.FACILITIES_HOURS_HISTORY);
        add(routes, HttpMethod.GET, ApiPaths.FACILITIES_HOURS_HISTORY);
        add(routes, HttpMethod.POST, ApiPaths.FACILITIES_MANAGERS);
        add(routes, HttpMethod.GET, ApiPaths.FACILITIES_MANAGERS);
        add(routes, HttpMethod.GET, ApiPaths.FACILITIES_CURRENT_MANAGER);
    }

    private static void addSecurity(Set<String> routes) {
        crud(routes, ApiPaths.GATES, ApiPaths.GATES_BY_ID);
        crud(routes, ApiPaths.VEHICLES, ApiPaths.VEHICLES_BY_ID);
        add(routes, HttpMethod.GET, ApiPaths.ACCESS_PERMITS);
        add(routes, HttpMethod.GET, ApiPaths.ACCESS_PERMITS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.ACCESS_PERMITS);
        add(routes, HttpMethod.PUT, ApiPaths.ACCESS_PERMITS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.VEHICLE_PERMIT);
        add(routes, HttpMethod.DELETE, ApiPaths.VEHICLE_PERMIT);
        add(routes, HttpMethod.GET, ApiPaths.GATE_GUARD_ASSIGNMENTS);
        add(routes, HttpMethod.GET, ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.GATE_GUARD_ASSIGNMENTS);
        add(routes, HttpMethod.PUT, ApiPaths.GATE_GUARD_ASSIGNMENTS_BY_ID);
        add(routes, HttpMethod.GET, ApiPaths.ENTRIES);
        add(routes, HttpMethod.GET, ApiPaths.ENTRIES_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.ENTRIES);
    }

    private static void addLeasing(Set<String> routes) {
        add(routes, HttpMethod.POST, ApiPaths.CONTRACT_PARTIES);
        add(routes, HttpMethod.GET, ApiPaths.CONTRACT_PARTIES);
        add(routes, HttpMethod.POST, ApiPaths.CONTRACT_RESIDENTS);
        add(routes, HttpMethod.GET, ApiPaths.CONTRACT_RESIDENTS);
        add(routes, HttpMethod.GET, ApiPaths.CONTRACTS);
        add(routes, HttpMethod.GET, ApiPaths.CONTRACTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.CONTRACTS);
        add(routes, HttpMethod.PUT, ApiPaths.CONTRACTS_BY_ID);
        add(routes, HttpMethod.PUT, ApiPaths.CONTRACTS_STATUS);
        add(routes, HttpMethod.GET, ApiPaths.INSTALLMENTS);
        add(routes, HttpMethod.GET, ApiPaths.INSTALLMENTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.INSTALLMENTS);
        add(routes, HttpMethod.PUT, ApiPaths.INSTALLMENTS_BY_ID);
    }

    private static void addFinance(Set<String> routes) {
        add(routes, HttpMethod.POST, ApiPaths.PAYMENTS_INSTALLMENT);
        add(routes, HttpMethod.POST, ApiPaths.PAYMENTS_WORK_ORDER);
        add(routes, HttpMethod.POST, ApiPaths.PAYMENTS_PAYROLL);
        add(routes, HttpMethod.GET, ApiPaths.PAYMENTS);
        add(routes, HttpMethod.GET, ApiPaths.PAYMENTS_BY_ID);
        add(routes, HttpMethod.GET, ApiPaths.BANK_ACCOUNTS);
        add(routes, HttpMethod.GET, ApiPaths.BANK_ACCOUNTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.BANK_ACCOUNTS);
        add(routes, HttpMethod.PUT, ApiPaths.BANK_ACCOUNTS_BY_ID);
    }

    private static void addRecruitment(Set<String> routes) {
        add(routes, HttpMethod.GET, ApiPaths.APPLICATIONS);
        add(routes, HttpMethod.POST, ApiPaths.APPLICATIONS);
        add(routes, HttpMethod.POST, ApiPaths.INTERVIEWS);
        add(routes, HttpMethod.PUT, ApiPaths.INTERVIEWS_RESULT);
        add(routes, HttpMethod.POST, ApiPaths.HIRE_AGREEMENTS);
    }

    private static void addWorkforce(Set<String> routes) {
        add(routes, HttpMethod.GET, ApiPaths.ASSIGNED_TASKS);
        add(routes, HttpMethod.GET, ApiPaths.ASSIGNED_TASKS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.ASSIGNED_TASKS);
        add(routes, HttpMethod.PUT, ApiPaths.ASSIGNED_TASKS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.ATTENDANCE);
        add(routes, HttpMethod.GET, ApiPaths.ATTENDANCE);
        add(routes, HttpMethod.POST, ApiPaths.PAYROLL_CLOSE);
        add(routes, HttpMethod.GET, ApiPaths.PAYROLL);
        add(routes, HttpMethod.POST, ApiPaths.STAFF_SALARY);
    }

    private static void addPerformance(Set<String> routes) {
        add(routes, HttpMethod.GET, ApiPaths.KPI_POLICIES);
        add(routes, HttpMethod.GET, ApiPaths.KPI_POLICIES_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.KPI_POLICIES);
        add(routes, HttpMethod.PUT, ApiPaths.KPI_POLICIES_BY_ID);
        add(routes, HttpMethod.GET, ApiPaths.PERFORMANCE_REVIEWS);
        add(routes, HttpMethod.GET, ApiPaths.PERFORMANCE_REVIEWS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.PERFORMANCE_REVIEWS);
        add(routes, HttpMethod.PUT, ApiPaths.PERFORMANCE_REVIEWS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.KPI_RECORDS);
        add(routes, HttpMethod.GET, ApiPaths.KPI_RECORDS);
        add(routes, HttpMethod.POST, ApiPaths.KPI_CLOSE);
        add(routes, HttpMethod.GET, ApiPaths.KPI_SUMMARIES);
    }

    private static void addMaintenance(Set<String> routes) {
        crud(routes, ApiPaths.COMPANIES, ApiPaths.COMPANIES_BY_ID);
        add(routes, HttpMethod.GET, ApiPaths.WORK_ORDERS);
        add(routes, HttpMethod.GET, ApiPaths.WORK_ORDERS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.WORK_ORDERS);
        add(routes, HttpMethod.PUT, ApiPaths.WORK_ORDERS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.WORK_ORDERS_ASSIGNMENTS);
        add(routes, HttpMethod.GET, ApiPaths.WORK_ORDERS_ASSIGNMENTS);
    }

    private static void addCommunication(Set<String> routes) {
        add(routes, HttpMethod.GET, ApiPaths.INTERNAL_REPORTS_UNREAD_COUNT);
        add(routes, HttpMethod.GET, ApiPaths.INTERNAL_REPORTS);
        add(routes, HttpMethod.GET, ApiPaths.INTERNAL_REPORTS_BY_ID);
        add(routes, HttpMethod.POST, ApiPaths.INTERNAL_REPORTS);
        add(routes, HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_BY_ID);
        add(routes, HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_READ);
        add(routes, HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_UNREAD);
        add(routes, HttpMethod.PUT, ApiPaths.INTERNAL_REPORTS_RESOLVE);
    }

    private static void crud(Set<String> routes, String collection, String item) {
        add(routes, HttpMethod.GET, collection);
        add(routes, HttpMethod.GET, item);
        add(routes, HttpMethod.POST, collection);
        add(routes, HttpMethod.PUT, item);
        add(routes, HttpMethod.DELETE, item);
    }

    private static void add(Set<String> routes, HttpMethod method, String apiPath) {
        routes.add(RouteKey.of(method, EndpointAuthorizationRules.pathPattern(apiPath)));
    }
}
