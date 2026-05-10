package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for compounds, buildings, units, and facilities.
 *
 * <p>Facility officers mutate operational property data. Accountants receive
 * selected read access where prices, contracts, and occupancy depend on
 * property records.
 */
final class PropertyAuthorizationRules {

    private PropertyAuthorizationRules() {
    }

    /**
     * Returns property route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow business administrators to maintain compound records.
                EndpointAuthorizationRules.crud(ApiPaths.COMPOUNDS, ApiPaths.COMPOUNDS_BY_ID,
                        RoleGroups.BUSINESS_ADMIN),
                // Allow facility officers to maintain buildings and finance to read them.
                EndpointAuthorizationRules.readableCrud(ApiPaths.BUILDINGS,
                        ApiPaths.BUILDINGS_BY_ID, RoleGroups.FACILITY,
                        RoleGroups.FACILITY_READERS),
                // Allow facility officers to maintain units and finance to read them.
                EndpointAuthorizationRules.readableCrud(ApiPaths.UNITS, ApiPaths.UNITS_BY_ID,
                        RoleGroups.FACILITY, RoleGroups.FACILITY_READERS),
                List.of(
                        // Allow finance to record unit price history.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.UNITS_PRICING_HISTORY, RoleGroups.FINANCE),
                        // Allow finance to inspect unit price history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.UNITS_PRICING_HISTORY, RoleGroups.FINANCE),
                        // Allow facility officers to record unit status history.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.UNITS_STATUS_HISTORY, RoleGroups.FACILITY),
                        // Allow facility officers to inspect unit status history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.UNITS_STATUS_HISTORY, RoleGroups.FACILITY)
                ),
                // Allow facility officers to maintain facilities and finance to read them.
                EndpointAuthorizationRules.readableCrud(ApiPaths.FACILITIES,
                        ApiPaths.FACILITIES_BY_ID, RoleGroups.FACILITY,
                        RoleGroups.FACILITY_READERS),
                List.of(
                        // Allow facility officers to record facility opening hours.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.FACILITIES_HOURS_HISTORY, RoleGroups.FACILITY),
                        // Allow facility officers to inspect facility opening hours.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.FACILITIES_HOURS_HISTORY, RoleGroups.FACILITY),
                        // Allow facility officers to assign facility managers.
                        EndpointAuthorizationRules.allow(HttpMethod.POST,
                                ApiPaths.FACILITIES_MANAGERS, RoleGroups.FACILITY),
                        // Allow facility officers to inspect facility manager history.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.FACILITIES_MANAGERS, RoleGroups.FACILITY),
                        // Allow facility officers to inspect the current facility manager.
                        EndpointAuthorizationRules.allow(HttpMethod.GET,
                                ApiPaths.FACILITIES_CURRENT_MANAGER, RoleGroups.FACILITY)
                )
        ).stream().flatMap(List::stream).toList();
    }
}
