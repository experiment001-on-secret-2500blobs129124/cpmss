package com.cpmss.platform.config.authorization;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every non-public controller route has an explicit role rule.
 *
 * <p>This test protects the default-deny posture: adding a controller route
 * without updating endpoint authorization should fail the route inventory
 * check.
 */
class EndpointAuthorizationCoverageTest {

    /**
     * Confirms the protected route inventory and authorization registry match.
     */
    @Test
    void coversEveryCurrentControllerRouteWithAnExplicitRoleRule() {
        Set<String> expectedRoutes = ControllerRouteInventory.protectedRoutes();
        List<String> actualRoutes = EndpointAuthorizationRules.roleRules()
                .stream()
                .map(RouteKey::of)
                .toList();

        assertThat(actualRoutes).doesNotHaveDuplicates();
        assertThat(actualRoutes).containsExactlyInAnyOrderElementsOf(expectedRoutes);
    }
}
