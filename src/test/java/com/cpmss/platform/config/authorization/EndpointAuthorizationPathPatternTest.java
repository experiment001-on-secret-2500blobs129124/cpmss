package com.cpmss.platform.config.authorization;

import com.cpmss.platform.common.ApiPaths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies conversion from controller path constants to security matchers.
 *
 * <p>Controllers use MVC path variables such as {@code {id}} while security
 * rules use wildcard segments for the same route shape.
 */
class EndpointAuthorizationPathPatternTest {

    /**
     * Confirms an MVC variable segment becomes a security wildcard segment.
     */
    @Test
    void convertsApiPathVariablesToSecurityMatcherWildcards() {
        String pattern = EndpointAuthorizationRules.pathPattern(ApiPaths.USERS_ROLE);

        assertThat(pattern).isEqualTo("/api/v1/users/*/role");
    }
}
