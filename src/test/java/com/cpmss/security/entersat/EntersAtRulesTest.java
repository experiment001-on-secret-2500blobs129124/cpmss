package com.cpmss.security.entersat;

import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.person.Person;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.PermitStatus;
import com.cpmss.security.accesspermit.PermitValidity;
import com.cpmss.security.common.SecurityErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies gate-entry ownership rules.
 *
 * <p>Route authorization decides who can call the entry endpoint; these rules
 * protect the resource-level constraint that a gate guard can act only from
 * their assigned post and only as themselves.
 */
class EntersAtRulesTest {

    private final EntersAtRules rules = new EntersAtRules();

    @Test
    void rejectsGateGuardOutsideAssignedGate() {
        assertThatThrownBy(() ->
                rules.validateGateGuardAssignedToGate(SystemRole.GATE_GUARD, false))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.GUARD_NOT_ASSIGNED));
    }

    @Test
    void allowsSecurityOfficerWithoutGuardPosting() {
        rules.validateGateGuardAssignedToGate(SystemRole.SECURITY_OFFICER, false);
    }

    @Test
    void rejectsGateGuardProcessingAsAnotherPerson() {
        UUID guardId = UUID.randomUUID();
        UUID otherPersonId = UUID.randomUUID();

        assertThatThrownBy(() -> rules.validateGateGuardProcessesOnlySelf(
                SystemRole.GATE_GUARD, guardId, otherPersonId))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.GUARD_NOT_ASSIGNED));
    }

    @Test
    void rejectsInactivePermitForGateEntry() {
        AccessPermit permit = activePermitThrough(LocalDate.of(2026, 5, 20));
        permit.setPermitStatus(PermitStatus.SUSPENDED);

        assertThatThrownBy(() -> rules.validatePermitUsableForEntry(
                permit, LocalDate.of(2026, 5, 10)))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.ACCESS_PERMIT_NOT_ACTIVE));
    }

    @Test
    void rejectsExpiredPermitForGateEntry() {
        AccessPermit permit = activePermitThrough(LocalDate.of(2026, 5, 9));

        assertThatThrownBy(() -> rules.validatePermitUsableForEntry(
                permit, LocalDate.of(2026, 5, 10)))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.ACCESS_PERMIT_EXPIRED));
    }

    @Test
    void rejectsBlacklistedPermitHolderForGateEntry() {
        AccessPermit permit = activePermitThrough(LocalDate.of(2026, 5, 20));
        Person holder = new Person();
        holder.setIsBlacklisted(true);
        permit.setPermitHolder(holder);

        assertThatThrownBy(() -> rules.validatePermitUsableForEntry(
                permit, LocalDate.of(2026, 5, 10)))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.ACCESS_PERMIT_HOLDER_BLACKLISTED));
    }

    private static AccessPermit activePermitThrough(LocalDate expiryDate) {
        AccessPermit permit = new AccessPermit();
        permit.setPermitStatus(PermitStatus.ACTIVE);
        permit.setValidity(new PermitValidity(LocalDate.of(2026, 5, 1), expiryDate));
        return permit;
    }

}
