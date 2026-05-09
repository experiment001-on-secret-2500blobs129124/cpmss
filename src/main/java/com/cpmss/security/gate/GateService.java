package com.cpmss.security.gate;

import com.cpmss.property.compound.Compound;
import com.cpmss.property.compound.CompoundRepository;
import com.cpmss.property.common.PropertyErrorCode;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.security.gate.dto.CreateGateRequest;
import com.cpmss.security.gate.dto.GateResponse;
import com.cpmss.security.gate.dto.UpdateGateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates gate lifecycle operations.
 *
 * <p>Gates are owned by a {@link Compound}. Gate number is unique
 * system-wide — validated via {@link GateRules}.
 *
 * @see GateRules
 * @see GateRepository
 */
@Service
public class GateService {

    private static final Logger log = LoggerFactory.getLogger(GateService.class);

    private final GateRepository repository;
    private final CompoundRepository compoundRepository;
    private final GateMapper mapper;
    private final GateRules rules = new GateRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         gate data access
     * @param compoundRepository compound data access (for FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public GateService(GateRepository repository,
                       CompoundRepository compoundRepository,
                       GateMapper mapper) {
        this.repository = repository;
        this.compoundRepository = compoundRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a gate by its unique identifier.
     *
     * @param id the gate's UUID primary key
     * @return the matching gate response
     * @throws ApiException if no gate exists with this ID
     */
    @Transactional(readOnly = true)
    public GateResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND)));
    }

    /**
     * Lists all gates with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of gate DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<GateResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new gate under the specified compound.
     *
     * @param request the create request with gate details and compound ID
     * @return the created gate response
     * @throws ApiException if the compound does not exist
     */
    @Transactional
    public GateResponse create(CreateGateRequest request) {
        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.COMPOUND_NOT_FOUND));

        rules.validateGateNoUnique(request.gateNo(), repository.existsByGateNo(request.gateNo()));

        Gate gate = Gate.builder()
                .gateNo(request.gateNo())
                .gateName(request.gateName())
                .gateType(request.gateType())
                .gateStatus(GateStatus.fromNullableLabel(request.gateStatus()))
                .compound(compound)
                .build();
        gate = repository.save(gate);
        log.info("Gate created: {} in {}", gate.getGateNo(), compound.getCompoundName());
        return mapper.toResponse(gate);
    }

    /**
     * Updates an existing gate.
     *
     * @param id      the gate's UUID
     * @param request the update request with new values
     * @return the updated gate response
     * @throws ApiException if the gate or compound does not exist
     */
    @Transactional
    public GateResponse update(UUID id, UpdateGateRequest request) {
        Gate gate = repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND));

        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.COMPOUND_NOT_FOUND));

        if (!gate.getGateNo().equals(request.gateNo())) {
            rules.validateGateNoUnique(request.gateNo(), repository.existsByGateNo(request.gateNo()));
        }

        gate.setGateNo(request.gateNo());
        gate.setGateName(request.gateName());
        gate.setGateType(request.gateType());
        gate.setGateStatus(GateStatus.fromNullableLabel(request.gateStatus()));
        gate.setCompound(compound);
        gate = repository.save(gate);
        log.info("Gate updated: {}", gate.getGateNo());
        return mapper.toResponse(gate);
    }

    /**
     * Deletes a gate by ID.
     *
     * @param id the gate's UUID
     * @throws ApiException if no gate exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Gate gate = repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND));
        repository.delete(gate);
        log.info("Gate deleted: {}", gate.getGateNo());
    }
}
