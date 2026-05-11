package com.cpmss.security.accesspermit;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityAccessRules;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies individual access-permit read authorization for checkpoint lookup.
 */
class AccessPermitReadAuthorizationTest {

    private final SecurityAccessRules rules = new SecurityAccessRules();

    @Test
    void permitsGateGuardToReadIndividualPermitForCheckpointVerification() {
        assertThatCode(() -> rules.validateCanReadAccessPermit(user(SystemRole.GATE_GUARD)))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsStaffReadingIndividualPermit() {
        assertThatThrownBy(() -> rules.validateCanReadAccessPermit(user(SystemRole.STAFF)))
                .isInstanceOfSatisfying(ApiException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(
                                SecurityErrorCode.SECURITY_RECORD_ACCESS_DENIED));
    }

    private CurrentUser user(SystemRole role) {
        return new CurrentUser(UUID.randomUUID(), UUID.randomUUID(), role, "user@example.com");
    }
}
