package com.cpmss.leasing.contract;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.leasing.common.LeasingAccessRules;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.contract.dto.ContractResponse;
import com.cpmss.leasing.contract.dto.CreateContractRequest;
import com.cpmss.leasing.contract.dto.UpdateContractRequest;
import com.cpmss.leasing.common.ContractPartyRole;
import com.cpmss.leasing.contractparty.ContractParty;
import com.cpmss.leasing.contractparty.ContractPartyRepository;
import com.cpmss.leasing.contractparty.dto.AddContractPartyRequest;
import com.cpmss.leasing.contractparty.dto.ContractPartyResponse;
import com.cpmss.leasing.common.LeasingErrorCode;
import com.cpmss.platform.exception.ApiException;
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
    private final CurrentUserService currentUserService;
    private final ContractRules rules = new ContractRules();
    private final LeasingAccessRules accessRules = new LeasingAccessRules();

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
                           ContractMapper mapper,
                           CurrentUserService currentUserService) {
        this.repository = repository;
        this.unitRepository = unitRepository;
        this.facilityRepository = facilityRepository;
        this.personRepository = personRepository;
        this.contractPartyRepository = contractPartyRepository;
        this.residesUnderRepository = residesUnderRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a contract by its unique identifier.
     *
     * @param id the contract's UUID primary key
     * @return the matching contract response
     * @throws ApiException if no contract exists with this ID
     */
    @Transactional(readOnly = true)
    public ContractResponse getById(UUID id) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(LeasingErrorCode.CONTRACT_NOT_FOUND)));
    }

    /**
     * Lists all contracts with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of contract DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<ContractResponse> listAll(Pageable pageable) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
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
     * @throws ApiException if target rule is violated or reference is duplicate

     */
    @Transactional
    public ContractResponse create(CreateContractRequest request) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        rules.validateExactlyOneTarget(request.unitId(), request.facilityId());
        rules.validateReferenceUnique(request.contractReference(),
                repository.existsByContractReference(request.contractReference()));

        Contract contract = Contract.builder()
                .contractReference(request.contractReference())
                .period(request.period())
                .contractType(request.contractType())
                .contractStatus(request.contractStatus())
                .paymentFrequency(request.paymentFrequency())
                .finalPrice(request.finalPrice())
                .securityDeposit(request.securityDeposit())
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
     * @throws ApiException if no contract exists with this ID
     */
    @Transactional
    public ContractResponse update(UUID id, UpdateContractRequest request) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        Contract contract = repository.findById(id)
                .orElseThrow(() -> new ApiException(LeasingErrorCode.CONTRACT_NOT_FOUND));

        rules.validateExactlyOneTarget(request.unitId(), request.facilityId());

        if (!contract.getContractReference().equals(request.contractReference())) {
            rules.validateReferenceUnique(request.contractReference(),
                    repository.existsByContractReference(request.contractReference()));
        }

        contract.setContractReference(request.contractReference());
        contract.setPeriod(request.period());
        contract.setContractType(request.contractType());
        contract.setContractStatus(request.contractStatus());
        contract.setPaymentFrequency(request.paymentFrequency());
        contract.setFinalPrice(request.finalPrice());
        contract.setSecurityDeposit(request.securityDeposit());
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
     * @throws ApiException if contract or person not found or primary signer exists
     */
    @Transactional
    public ContractPartyResponse addParty(UUID contractId, AddContractPartyRequest request) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        Contract contract = findContractOrThrow(contractId);
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new ApiException(LeasingErrorCode.PERSON_NOT_FOUND));

        if (request.role() == ContractPartyRole.PRIMARY_SIGNER) {
            boolean hasPrimary = contractPartyRepository.findByContractId(contractId)
                    .stream().anyMatch(cp -> cp.getRole() == ContractPartyRole.PRIMARY_SIGNER);
            if (hasPrimary) {
                throw new ApiException(LeasingErrorCode.PRIMARY_SIGNER_DUPLICATE);
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
     * @throws ApiException if contract not found
     */
    @Transactional(readOnly = true)
    public java.util.List<ContractPartyResponse> getParties(UUID contractId) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
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
     * @throws ApiException if contract or person not found
     */
    @Transactional
    public PersonResidesUnderResponse addResident(UUID contractId,
                                                   AddPersonResidesUnderRequest request) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        Contract contract = findContractOrThrow(contractId);
        Person resident = personRepository.findById(request.residentId())
                .orElseThrow(() -> new ApiException(LeasingErrorCode.PERSON_NOT_FOUND));

        PersonResidesUnder record = new PersonResidesUnder();
        record.setResident(resident);
        record.setContract(contract);
        record.setResidencyPeriod(request.residencyPeriod());
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
     * @throws ApiException if contract not found
     */
    @Transactional(readOnly = true)
    public java.util.List<PersonResidesUnderResponse> getResidents(UUID contractId) {
        accessRules.requireLeasingAuthority(currentUserService.currentUser());
        findContractOrThrow(contractId);
        return residesUnderRepository.findByContractId(contractId)
                .stream().map(this::toResidentResponse).toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Contract findContractOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(LeasingErrorCode.CONTRACT_NOT_FOUND));
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
                r.getResidencyPeriod(),
                r.getHouseholdRelationship());
    }

    private Unit resolveUnit(UUID id) {
        if (id == null) {
            return null;
        }
        return unitRepository.findById(id)
                .orElseThrow(() -> new ApiException(LeasingErrorCode.UNIT_NOT_FOUND));
    }

    private Facility resolveFacility(UUID id) {
        if (id == null) {
            return null;
        }
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ApiException(LeasingErrorCode.FACILITY_NOT_FOUND));
    }
}
