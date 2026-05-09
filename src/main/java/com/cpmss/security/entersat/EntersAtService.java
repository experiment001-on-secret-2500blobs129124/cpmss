package com.cpmss.security.entersat;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.accesspermit.AccessPermit;
import com.cpmss.security.accesspermit.AccessPermitRepository;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.security.entersat.dto.CreateEntersAtRequest;
import com.cpmss.security.entersat.dto.EntersAtResponse;
import com.cpmss.security.gate.Gate;
import com.cpmss.security.gate.GateRepository;
import com.cpmss.security.gateguardassignment.GateGuardAssignmentRepository;
import com.cpmss.security.vehicle.LicensePlate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates gate access event operations.
 *
 * <p>Gate entries are immutable audit records — create only, no
 * update or delete. Exactly one entry method (permit or manual
 * plate) must be provided per event — enforced by {@link EntersAtRules}.
 *
 * @see EntersAtRules
 * @see EntersAtRepository
 */
@Service
public class EntersAtService {

    private static final Logger log = LoggerFactory.getLogger(EntersAtService.class);

    private final EntersAtRepository repository;
    private final GateRepository gateRepository;
    private final AccessPermitRepository accessPermitRepository;
    private final PersonRepository personRepository;
    private final GateGuardAssignmentRepository gateGuardAssignmentRepository;
    private final CurrentUserService currentUserService;
    private final EntersAtMapper mapper;
    private final EntersAtRules rules = new EntersAtRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository                    entry data access
     * @param gateRepository                gate data access (FK lookup)
     * @param accessPermitRepository        permit data access (FK lookup)
     * @param personRepository              person data access (FK lookup)
     * @param gateGuardAssignmentRepository guard posting data access
     * @param currentUserService            authenticated user resolver
     * @param mapper                        entity-DTO mapper
     */
    public EntersAtService(EntersAtRepository repository,
                           GateRepository gateRepository,
                           AccessPermitRepository accessPermitRepository,
                           PersonRepository personRepository,
                           GateGuardAssignmentRepository gateGuardAssignmentRepository,
                           CurrentUserService currentUserService,
                           EntersAtMapper mapper) {
        this.repository = repository;
        this.gateRepository = gateRepository;
        this.accessPermitRepository = accessPermitRepository;
        this.personRepository = personRepository;
        this.gateGuardAssignmentRepository = gateGuardAssignmentRepository;
        this.currentUserService = currentUserService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a gate entry by its unique identifier.
     *
     * @param id the entry's UUID primary key
     * @return the matching entry response
     * @throws ApiException if no entry exists with this ID
     */
    @Transactional(readOnly = true)
    public EntersAtResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_ENTRY_NOT_FOUND)));
    }

    /**
     * Lists all gate entries with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of entry DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<EntersAtResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Records a new gate access event.
     *
     * <p>Gate entries are immutable — once recorded they cannot
     * be modified or deleted. Gate guards can create entries only for gates
     * where they have an active posting at the event timestamp.
     *
     * @param request the entry details
     * @return the created entry response
     * @throws ApiException if entry method rule is violated, access is denied,
     *                      or a reference is missing
     */
    @Transactional
    public EntersAtResponse create(CreateEntersAtRequest request) {
        rules.validateExactlyOneEntryMethod(request.permitId(), request.manualPlateEntry());
        CurrentUser actor = currentUserService.currentUser();
        Person currentGuard = resolveCurrentGuardIfNeeded(actor, request);

        Gate gate = gateRepository.findById(request.gateId())
            .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND));

        EntersAt entry = EntersAt.builder()
                .gate(gate)
                .permit(resolvePermit(request.permitId()))
                .manualPlateEntry(LicensePlate.ofNullable(request.manualPlateEntry()))
                .enteredAt(request.enteredAt())
                .direction(GateDirection.fromLabel(request.direction()))
                .purpose(request.purpose())
                .processedBy(resolveProcessedBy(request, actor, currentGuard))
                .requestedBy(resolvePersonNullable(request.requestedById()))
                .build();
        entry = repository.save(entry);
        log.info("Gate entry recorded: {} at gate {} direction {}",
                entry.getId(), request.gateId(), request.direction());
        return mapper.toResponse(entry);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Person resolveCurrentGuardIfNeeded(CurrentUser actor, CreateEntersAtRequest request) {
        if (!actor.hasRole(SystemRole.GATE_GUARD)) {
            return null;
        }
        UUID guardPersonId = actor.requirePersonId("Gate entry logging");
        boolean assignedToGate = gateGuardAssignmentRepository.existsActivePostingAtGate(
                guardPersonId, request.gateId(), request.enteredAt());
        rules.validateGateGuardAssignedToGate(actor.systemRole(), assignedToGate);
        rules.validateGateGuardProcessesOnlySelf(
                actor.systemRole(), guardPersonId, request.processedById());
        return personRepository.findById(guardPersonId)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
    }

    private Person resolveProcessedBy(CreateEntersAtRequest request,
                                      CurrentUser actor,
                                      Person currentGuard) {
        if (!actor.hasRole(SystemRole.GATE_GUARD)) {
            return resolvePersonNullable(request.processedById());
        }
        boolean anonymousEntry = request.permitId() == null;
        if (anonymousEntry || request.processedById() != null) {
            return currentGuard;
        }
        return null;
    }

    private AccessPermit resolvePermit(UUID id) {
        if (id == null) {
            return null;
        }
        return accessPermitRepository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.ACCESS_PERMIT_NOT_FOUND));
    }

    private Person resolvePersonNullable(UUID id) {
        if (id == null) {
            return null;
        }
        return personRepository.findById(id)
                .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
    }
}
