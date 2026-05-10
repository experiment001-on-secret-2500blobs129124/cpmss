package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for vendors and work-order routes.
 *
 * <p>Facility officers own maintenance operations. Accountants can read
 * vendor and work-order data because those records drive payment execution.
 */
final class MaintenanceAuthorizationRules {

    private MaintenanceAuthorizationRules() {
    }

    /**
     * Returns maintenance route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow facility officers to maintain vendors and finance to read them.
                EndpointAuthorizationRules.readableCrud(ApiPaths.COMPANIES,
                        ApiPaths.COMPANIES_BY_ID, RoleGroups.FACILITY,
                        RoleGroups.FACILITY_READERS),
                List.of(
                        // Allow facility and finance readers to browse work orders.
                        EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.WORK_ORDERS,
                                RoleGroups.FACILITY_READERS),
                        // Allow facility and finance readers to inspect a work order.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.WORK_ORDERS_BY_ID, RoleGroups.FACILITY_READERS),
                        // Allow facility officers to create work orders.
                        EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.WORK_ORDERS,
                                RoleGroups.FACILITY),
                        // Allow facility officers to update work orders.
                        EndpointAuthorizationRules.allow(HttpMethod.PUT,
                                ApiPaths.WORK_ORDERS_BY_ID, RoleGroups.FACILITY),
                        // Allow facility officers to assign vendors to work orders.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.WORK_ORDERS_ASSIGNMENTS, RoleGroups.FACILITY),
                        // Allow facility and finance readers to inspect vendor assignments.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.WORK_ORDERS_ASSIGNMENTS, RoleGroups.FACILITY_READERS)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
