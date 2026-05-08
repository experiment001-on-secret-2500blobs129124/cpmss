package com.cpmss.leasing.installment;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.leasing.contract.Contract;
import com.cpmss.leasing.contract.ContractRepository;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.leasing.installment.dto.CreateInstallmentRequest;
import com.cpmss.leasing.installment.dto.InstallmentResponse;
import com.cpmss.leasing.installment.dto.UpdateInstallmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates installment lifecycle operations.
 *
 * <p>Installments are child records of {@link Contract}. They are
 * permanent financial records — never deleted.
 *
 * @see InstallmentRepository
 */
@Service
public class InstallmentService {

    private static final Logger log = LoggerFactory.getLogger(InstallmentService.class);

    private final InstallmentRepository repository;
    private final ContractRepository contractRepository;
    private final InstallmentMapper mapper;
    private final InstallmentRules rules = new InstallmentRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         installment data access
     * @param contractRepository contract data access (parent FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public InstallmentService(InstallmentRepository repository,
                              ContractRepository contractRepository,
                              InstallmentMapper mapper) {
        this.repository = repository;
        this.contractRepository = contractRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves an installment by its unique identifier.
     *
     * @param id the installment's UUID primary key
     * @return the matching installment response
     * @throws ResourceNotFoundException if no installment exists with this ID
     */
    @Transactional(readOnly = true)
    public InstallmentResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all installments with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of installment DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<InstallmentResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new installment under a contract.
     *
     * @param request the create request with installment details and contract ID
     * @return the created installment response
     * @throws ResourceNotFoundException if the contract does not exist
     */
    @Transactional
    public InstallmentResponse create(CreateInstallmentRequest request) {
        rules.validateAmountPositive(request.amountExpected());

        Contract contract = contractRepository.findById(request.contractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract", request.contractId()));

        Installment installment = Installment.builder()
                .installmentType(request.installmentType())
                .dueDate(request.dueDate())
                .installmentStatus(request.installmentStatus())
                .amountExpected(request.amountExpected())
                .contract(contract)
                .build();
        installment = repository.save(installment);
        log.info("Installment created: {} for contract {}", installment.getId(), request.contractId());
        return mapper.toResponse(installment);
    }

    /**
     * Updates an existing installment.
     *
     * <p>Contract association is immutable — cannot be changed after creation.
     *
     * @param id      the installment's UUID
     * @param request the update request with new values
     * @return the updated installment response
     * @throws ResourceNotFoundException if no installment exists with this ID
     */
    @Transactional
    public InstallmentResponse update(UUID id, UpdateInstallmentRequest request) {
        Installment installment = findOrThrow(id);

        rules.validateAmountPositive(request.amountExpected());
        rules.validateStatusTransition(installment.getInstallmentStatus(),
                request.installmentStatus());

        installment.setInstallmentType(request.installmentType());
        installment.setDueDate(request.dueDate());
        installment.setInstallmentStatus(request.installmentStatus());
        installment.setAmountExpected(request.amountExpected());
        installment = repository.save(installment);
        log.info("Installment updated: {}", installment.getId());
        return mapper.toResponse(installment);
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Installment findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment", id));
    }
}
