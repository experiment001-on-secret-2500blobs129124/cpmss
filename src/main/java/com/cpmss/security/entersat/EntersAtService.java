package com.cpmss.security.entersat;

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
    private final EntersAtMapper mapper;
    private final EntersAtRules rules = new EntersAtRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository             entry data access
     * @param gateRepository         gate data access (FK lookup)
     * @param accessPermitRepository permit data access (FK lookup)
     * @param personRepository       person data access (FK lookup)
     * @param mapper                 entity-DTO mapper
     */
    public EntersAtService(EntersAtRepository repository,
                           GateRepository gateRepository,
                           AccessPermitRepository accessPermitRepository,
                           PersonRepository personRepository,
                           EntersAtMapper mapper) {
        this.repository = repository;
        this.gateRepository = gateRepository;
        this.accessPermitRepository = accessPermitRepository;
        this.personRepository = personRepository;
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
     * be modified or deleted.
     *
     * @param request the entry details
     * @return the created entry response
    * @throws ApiException if entry method rule is violated or a reference is missing
     */
    @Transactional
    public EntersAtResponse create(CreateEntersAtRequest request) {
        rules.validateExactlyOneEntryMethod(request.permitId(), request.manualPlateEntry());

        Gate gate = gateRepository.findById(request.gateId())
            .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND));

        EntersAt entry = EntersAt.builder()
                .gate(gate)
                .permit(resolvePermit(request.permitId()))
                .manualPlateEntry(LicensePlate.ofNullable(request.manualPlateEntry()))
                .enteredAt(request.enteredAt())
                .direction(GateDirection.fromLabel(request.direction()))
                .purpose(request.purpose())
                .processedBy(resolvePersonNullable(request.processedById()))
                .requestedBy(resolvePersonNullable(request.requestedById()))
                .build();
        entry = repository.save(entry);
        log.info("Gate entry recorded: {} at gate {} direction {}",
                entry.getId(), request.gateId(), request.direction());
        return mapper.toResponse(entry);
    }

    // ── Private helpers ─────────────────────────────────────────────────

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
