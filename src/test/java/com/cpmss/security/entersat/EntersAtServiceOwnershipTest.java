package com.cpmss.security.entersat;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermitRepository;
import com.cpmss.security.entersat.dto.CreateEntersAtRequest;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.security.entersat.dto.EntersAtResponse;
import com.cpmss.security.gate.Gate;
import com.cpmss.security.gate.GateRepository;
import com.cpmss.security.gateguardassignment.GateGuardAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies service-level ownership checks for gate entry creation.
 *
 * <p>The service must not rely only on route roles. A gate guard can create
 * entry records only for the gate where they are actively posted, and anonymous
 * entries are attributed to the authenticated guard.
 */
@ExtendWith(MockitoExtension.class)
class EntersAtServiceOwnershipTest {

    @Mock
    private EntersAtRepository entersAtRepository;

    @Mock
    private GateRepository gateRepository;

    @Mock
    private AccessPermitRepository accessPermitRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GateGuardAssignmentRepository gateGuardAssignmentRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private EntersAtMapper mapper;

    @Test
    void rejectsGateGuardCreatingEntryForUnassignedGate() {
        UUID guardId = UUID.randomUUID();
        UUID gateId = UUID.randomUUID();
        Instant enteredAt = Instant.parse("2026-05-09T09:00:00Z");
        when(currentUserService.currentUser()).thenReturn(gateGuard(guardId));
        when(gateGuardAssignmentRepository.existsActivePostingAtGate(
                guardId, gateId, enteredAt)).thenReturn(false);

        assertThatThrownBy(() -> service().create(anonymousEntry(gateId, enteredAt, null)))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(SecurityErrorCode.GUARD_NOT_ASSIGNED));
        verify(entersAtRepository, never()).save(any());
    }

    @Test
    void defaultsAnonymousGateGuardEntryToAuthenticatedGuard() {
        UUID guardId = UUID.randomUUID();
        UUID gateId = UUID.randomUUID();
        Instant enteredAt = Instant.parse("2026-05-09T09:00:00Z");
        Person guard = new Person();
        guard.setId(guardId);
        Gate gate = new Gate();
        gate.setId(gateId);
        when(currentUserService.currentUser()).thenReturn(gateGuard(guardId));
        when(gateGuardAssignmentRepository.existsActivePostingAtGate(
                guardId, gateId, enteredAt)).thenReturn(true);
        when(personRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(entersAtRepository.save(any(EntersAt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(EntersAt.class))).thenReturn(response());

        service().create(anonymousEntry(gateId, enteredAt, null));

        ArgumentCaptor<EntersAt> entryCaptor = ArgumentCaptor.forClass(EntersAt.class);
        verify(entersAtRepository).save(entryCaptor.capture());
        assertThat(entryCaptor.getValue().getProcessedBy()).isEqualTo(guard);
    }

    private EntersAtService service() {
        return new EntersAtService(
                entersAtRepository,
                gateRepository,
                accessPermitRepository,
                personRepository,
                gateGuardAssignmentRepository,
                currentUserService,
                mapper);
    }

    private static CurrentUser gateGuard(UUID personId) {
        return new CurrentUser(
                UUID.randomUUID(), personId, SystemRole.GATE_GUARD, "guard@example.com");
    }

    private static CreateEntersAtRequest anonymousEntry(UUID gateId,
                                                        Instant enteredAt,
                                                        UUID processedById) {
        return new CreateEntersAtRequest(
                gateId,
                null,
                "abc-123",
                enteredAt,
                "In",
                "Delivery",
                processedById,
                null);
    }

    private static EntersAtResponse response() {
        return new EntersAtResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "ABC123",
                Instant.parse("2026-05-09T09:00:00Z"),
                "In",
                "Delivery",
                UUID.randomUUID(),
                null,
                Instant.parse("2026-05-09T09:00:01Z"));
    }
}
