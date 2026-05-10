package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Role gates for payment and bank-account routes.
 *
 * <p>Payment creation endpoints are separated by subtype so finance can keep
 * the parent payment record aligned with its installment, work-order, or
 * payroll child record.
 */
final class FinanceAuthorizationRules {

    private FinanceAuthorizationRules() {
    }

    /**
     * Returns finance route authorization rules.
     *
     * @return immutable endpoint role rules
     */
    static List<EndpointAuthorizationRule> rules() {
        return List.of(
                // Allow finance to receive installment payments.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PAYMENTS_INSTALLMENT,
                        RoleGroups.FINANCE),
                // Allow finance to pay work-order vendors.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PAYMENTS_WORK_ORDER,
                        RoleGroups.FINANCE),
                // Allow finance to pay payroll obligations.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.PAYMENTS_PAYROLL,
                        RoleGroups.FINANCE),
                // Allow finance to browse payments.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PAYMENTS,
                        RoleGroups.FINANCE),
                // Allow finance to inspect a payment.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.PAYMENTS_BY_ID,
                        RoleGroups.FINANCE),
                // Allow staff-based users to browse service-filtered bank accounts.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.BANK_ACCOUNTS,
                        RoleGroups.STAFF_SELF_READERS),
                // Allow staff-based users to inspect service-filtered bank accounts.
                EndpointAuthorizationRules.allow(HttpMethod.GET, ApiPaths.BANK_ACCOUNTS_BY_ID,
                        RoleGroups.STAFF_SELF_READERS),
                // Allow finance to create bank accounts.
                EndpointAuthorizationRules.allow(HttpMethod.POST, ApiPaths.BANK_ACCOUNTS,
                        RoleGroups.FINANCE),
                // Allow finance to update bank account details.
                EndpointAuthorizationRules.allow(HttpMethod.PUT, ApiPaths.BANK_ACCOUNTS_BY_ID,
                        RoleGroups.FINANCE)
        );
    }
}
