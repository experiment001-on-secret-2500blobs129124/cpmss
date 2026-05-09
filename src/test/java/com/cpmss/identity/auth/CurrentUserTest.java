package com.cpmss.identity.auth;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies authenticated user value behavior used by ownership checks.
 *
 * <p>Ownership rules need a linked person for actions performed by real staff
 * members, such as gate guards and department managers.
 */
class CurrentUserTest {

    @Test
    void returnsLinkedPersonIdWhenPresent() {
        UUID personId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(
                UUID.randomUUID(), personId, SystemRole.GATE_GUARD, "guard@example.com");

        assertThat(currentUser.requirePersonId("Gate entry logging")).isEqualTo(personId);
    }

    @Test
    void rejectsPersonScopedActionWithoutLinkedPerson() {
        CurrentUser currentUser = new CurrentUser(
                UUID.randomUUID(), null, SystemRole.GATE_GUARD, "guard@example.com");

        assertThatThrownBy(() -> currentUser.requirePersonId("Gate entry logging"))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(CommonErrorCode.ACCESS_DENIED));
    }
}
