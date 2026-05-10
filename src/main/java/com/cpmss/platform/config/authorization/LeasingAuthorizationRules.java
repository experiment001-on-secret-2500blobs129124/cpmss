package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for leasing contracts, parties, residents, and installments.
 *
 * <p>Accountants own broad leasing operations because contracts and
 * installments directly affect receivables and payment allocation.
 */
final class LeasingAuthorizationRules {

    private LeasingAuthorizationRules() {
    }

    /**
     * Returns leasing route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow finance to add people or organizations to contracts.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.CONTRACT_PARTIES,
                        RoleGroups.FINANCE),
                // Allow finance to inspect contract parties.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.CONTRACT_PARTIES,
                        RoleGroups.FINANCE),
                // Allow finance to record residents under a contract.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.CONTRACT_RESIDENTS,
                        RoleGroups.FINANCE),
                // Allow finance to inspect contract residents.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.CONTRACT_RESIDENTS,
                        RoleGroups.FINANCE),
                // Allow finance to browse contracts.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.CONTRACTS,
                        RoleGroups.FINANCE),
                // Allow finance to inspect a contract.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.CONTRACTS_BY_ID,
                        RoleGroups.FINANCE),
                // Allow finance to create lease contracts.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.CONTRACTS,
                        RoleGroups.FINANCE),
                // Allow finance to update lease contracts.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.CONTRACTS_BY_ID,
                        RoleGroups.FINANCE),
                // Allow finance to transition lease contract status.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.CONTRACTS_STATUS,
                        RoleGroups.FINANCE),
                // Allow finance to browse installments.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INSTALLMENTS,
                        RoleGroups.FINANCE),
                // Allow finance to inspect an installment.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.INSTALLMENTS_BY_ID,
                        RoleGroups.FINANCE),
                // Allow finance to create installment obligations.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.INSTALLMENTS,
                        RoleGroups.FINANCE),
                // Allow finance to update installment status/details.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.INSTALLMENTS_BY_ID,
                        RoleGroups.FINANCE)
        );
    }
}
