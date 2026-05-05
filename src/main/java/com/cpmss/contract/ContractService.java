package com.cpmss.contract;

import com.cpmss.common.PagedResponse;
import com.cpmss.contract.dto.ContractResponse;
import com.cpmss.contract.dto.CreateContractRequest;
import com.cpmss.contract.dto.UpdateContractRequest;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.facility.Facility;
import com.cpmss.facility.FacilityRepository;
import com.cpmss.unit.Unit;
import com.cpmss.unit.UnitRepository;
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
    private final ContractMapper mapper;
    private final ContractRules rules = new ContractRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         contract data access
     * @param unitRepository     unit data access (target FK lookup)
     * @param facilityRepository facility data access (target FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public ContractService(ContractRepository repository,
                           UnitRepository unitRepository,
                           FacilityRepository facilityRepository,
                           ContractMapper mapper) {
        this.repository = repository;
        this.unitRepository = unitRepository;
        this.facilityRepository = facilityRepository;
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
     * @throws com.cpmss.exception.BusinessException if target rule is violated
     * @throws com.cpmss.exception.ConflictException  if reference is duplicate
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

    // ── Private helpers ─────────────────────────────────────────────────

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
