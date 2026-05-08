package com.cpmss.leasing.contract;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.contract.dto.ContractResponse;
import com.cpmss.leasing.contract.dto.CreateContractRequest;
import com.cpmss.leasing.contract.dto.UpdateContractRequest;
import com.cpmss.leasing.contractparty.ContractParty;
import com.cpmss.leasing.contractparty.ContractPartyRepository;
import com.cpmss.leasing.contractparty.dto.AddContractPartyRequest;
import com.cpmss.leasing.contractparty.dto.ContractPartyResponse;
import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.property.facility.Facility;
import com.cpmss.property.facility.FacilityRepository;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.leasing.personresidesunder.PersonResidesUnder;
import com.cpmss.leasing.personresidesunder.PersonResidesUnderRepository;
import com.cpmss.leasing.personresidesunder.dto.AddPersonResidesUnderRequest;
import com.cpmss.leasing.personresidesunder.dto.PersonResidesUnderResponse;
import com.cpmss.property.unit.Unit;
import com.cpmss.property.unit.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates contract lifecycle operations.
 *
 * <p>A contract covers exactly one target — either a {@link Unit}
 * (Residential) or a {@link Facility} (Commercial). The mutual
 * exclusion constraint is enforced by {@link ContractRules}.
 *
 * <p>Contracts are permanent records — closed by status change
 * (Terminated, Expired), never by deletion.
 *
 * @see ContractRules
 * @see ContractRepository
 */
@Service
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository repository;
    private final UnitRepository unitRepository;
    private final FacilityRepository facilityRepository;
    private final PersonRepository personRepository;
    private final ContractPartyRepository contractPartyRepository;
    private final PersonResidesUnderRepository residesUnderRepository;
    private final ContractMapper mapper;
    private final ContractRules rules = new ContractRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository                contract data access
     * @param unitRepository            unit data access (target FK lookup)
     * @param facilityRepository        facility data access (target FK lookup)
     * @param personRepository          person data access (party/resident FK lookup)
     * @param contractPartyRepository   contract party data access
     * @param residesUnderRepository    person-resides-under data access
     * @param mapper                    entity-DTO mapper
     */
    public ContractService(ContractRepository repository,
                           UnitRepository unitRepository,
                           FacilityRepository facilityRepository,
                           PersonRepository personRepository,
                           ContractPartyRepository contractPartyRepository,
                           PersonResidesUnderRepository residesUnderRepository,
                           ContractMapper mapper) {
        this.repository = repository;
        this.unitRepository = unitRepository;
        this.facilityRepository = facilityRepository;
        this.personRepository = personRepository;
        this.contractPartyRepository = contractPartyRepository;
        this.residesUnderRepository = residesUnderRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a contract by its unique identifier.
     *
     * @param id the contract's UUID primary key
     * @return the matching contract response
     * @throws ResourceNotFoundException if no contract exists with this ID
     */
    @Transactional(readOnly = true)
    public ContractResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", id)));
    }

    /**
     * Lists all contracts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of contract DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<ContractResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new contract with exactly one target.
     *
     * <p>Validates mutual exclusion (unit XOR facility) and
     * contract reference uniqueness before saving.
     *
     * @param request the create request with contract details and target ID
     * @return the created contract response
     * @throws com.cpmss.platform.exception.BusinessException if target rule is violated
     * @throws com.cpmss.platform.exception.ConflictException  if reference is duplicate
     */
    @Transactional
    public ContractResponse create(CreateContractRequest request) {
        rules.validateExactlyOneTarget(request.unitId(), request.facilityId());
        rules.validateReferenceUnique(request.contractReference(),
                repository.existsByContractReference(request.contractReference()));

        Contract contract = Contract.builder()
                .contractReference(request.contractReference())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .contractType(request.contractType())
                .contractStatus(request.contractStatus())
                .paymentFrequency(request.paymentFrequency())
                .finalPrice(request.finalPrice())
                .securityDepositAmount(request.securityDepositAmount())
                .renewalTerms(request.renewalTerms())
                .unit(resolveUnit(request.unitId()))
                .facility(resolveFacility(request.facilityId()))
                .build();
        contract = repository.save(contract);
        log.info("Contract created: {}", contract.getContractReference());
        return mapper.toResponse(contract);
    }

    /**
     * Updates an existing contract.
     *
     * @param id      the contract's UUID
     * @param request the update request with new values
     * @return the updated contract response
     * @throws ResourceNotFoundException if no contract exists with this ID
     */
    @Transactional
    public ContractResponse update(UUID id, UpdateContractRequest request) {
        Contract contract = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", id));

        rules.validateExactlyOneTarget(request.unitId(), request.facilityId());

        if (!contract.getContractReference().equals(request.contractReference())) {
            rules.validateReferenceUnique(request.contractReference(),
                    repository.existsByContractReference(request.contractReference()));
        }

        contract.setContractReference(request.contractReference());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setContractType(request.contractType());
        contract.setContractStatus(request.contractStatus());
        contract.setPaymentFrequency(request.paymentFrequency());
        contract.setFinalPrice(request.finalPrice());
        contract.setSecurityDepositAmount(request.securityDepositAmount());
        contract.setRenewalTerms(request.renewalTerms());
        contract.setUnit(resolveUnit(request.unitId()));
        contract.setFacility(resolveFacility(request.facilityId()));
        contract = repository.save(contract);
        log.info("Contract updated: {}", contract.getContractReference());
        return mapper.toResponse(contract);
    }

    // ── Contract Party Management ───────────────────────────────────────

    /**
     * Adds a party to a contract.
     *
     * <p>Validates: primary signer uniqueness (each contract may have
     * at most one Primary Signer — V2 partial unique index).
     *
     * @param contractId the contract's UUID
     * @param request    the party details
     * @return the created contract party response
     * @throws ResourceNotFoundException if contract or person not found
     * @throws BusinessException if a Primary Signer already exists
     */
    @Transactional
    public ContractPartyResponse addParty(UUID contractId, AddContractPartyRequest request) {
        Contract contract = findContractOrThrow(contractId);
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.personId()));

        if ("Primary Signer".equals(request.role())) {
            boolean hasPrimary = contractPartyRepository.findByContractId(contractId)
                    .stream().anyMatch(cp -> "Primary Signer".equals(cp.getRole()));
            if (hasPrimary) {
                throw new BusinessException(
                        "Contract already has a Primary Signer — only one is allowed");
            }
        }

        ContractParty party = new ContractParty();
        party.setPerson(person);
        party.setContract(contract);
        party.setRole(request.role());
        party.setDateSigned(request.dateSigned());
        party = contractPartyRepository.save(party);
        log.info("Party added to contract {}: person {} as {}",
                contractId, request.personId(), request.role());
        return toPartyResponse(party);
    }

    /**
     * Lists all parties for a contract.
     *
     * @param contractId the contract's UUID
     * @return list of contract party responses
     * @throws ResourceNotFoundException if contract not found
     */
    @Transactional(readOnly = true)
    public java.util.List<ContractPartyResponse> getParties(UUID contractId) {
        findContractOrThrow(contractId);
        return contractPartyRepository.findByContractId(contractId)
                .stream().map(this::toPartyResponse).toList();
    }

    // ── Resident Management ─────────────────────────────────────────────

    /**
     * Adds a resident under a contract.
     *
     * @param contractId the contract's UUID
     * @param request    the resident details
     * @return the created residency response
     * @throws ResourceNotFoundException if contract or person not found
     */
    @Transactional
    public PersonResidesUnderResponse addResident(UUID contractId,
                                                   AddPersonResidesUnderRequest request) {
        Contract contract = findContractOrThrow(contractId);
        Person resident = personRepository.findById(request.residentId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.residentId()));

        PersonResidesUnder record = new PersonResidesUnder();
        record.setResident(resident);
        record.setContract(contract);
        record.setMoveInDate(request.moveInDate());
        record.setHouseholdRelationship(request.householdRelationship());
        record = residesUnderRepository.save(record);
        log.info("Resident added to contract {}: person {} as {}",
                contractId, request.residentId(), request.householdRelationship());
        return toResidentResponse(record);
    }

    /**
     * Lists all residents for a contract.
     *
     * @param contractId the contract's UUID
     * @return list of residency responses
     * @throws ResourceNotFoundException if contract not found
     */
    @Transactional(readOnly = true)
    public java.util.List<PersonResidesUnderResponse> getResidents(UUID contractId) {
        findContractOrThrow(contractId);
        return residesUnderRepository.findByContractId(contractId)
                .stream().map(this::toResidentResponse).toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Contract findContractOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", id));
    }

    private ContractPartyResponse toPartyResponse(ContractParty cp) {
        return new ContractPartyResponse(
                cp.getPerson().getId(),
                cp.getContract().getId(),
                cp.getRole(),
                cp.getDateSigned());
    }

    private PersonResidesUnderResponse toResidentResponse(PersonResidesUnder r) {
        return new PersonResidesUnderResponse(
                r.getResident().getId(),
                r.getContract().getId(),
                r.getMoveInDate(),
                r.getMoveOutDate(),
                r.getHouseholdRelationship());
    }

    private Unit resolveUnit(UUID id) {
        if (id == null) {
            return null;
        }
        return unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", id));
    }

    private Facility resolveFacility(UUID id) {
        if (id == null) {
            return null;
        }
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
    }
}
