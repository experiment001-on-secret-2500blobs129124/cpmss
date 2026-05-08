package com.cpmss.property.compound;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.property.compound.dto.CompoundResponse;
import com.cpmss.property.compound.dto.CreateCompoundRequest;
import com.cpmss.property.compound.dto.UpdateCompoundRequest;
import com.cpmss.platform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates compound lifecycle operations.
 *
 * <p>Handles CRUD for {@link Compound} entities. Compound is the
 * root entity in the system — all buildings, gates, and
 * organizational structures belong to exactly one compound.
 *
 * @see CompoundRepository
 */
@Service
public class CompoundService {

    private static final Logger log = LoggerFactory.getLogger(CompoundService.class);

    private final CompoundRepository repository;
    private final CompoundMapper mapper;

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository compound data access
     * @param mapper     entity-DTO mapper
     */
    public CompoundService(CompoundRepository repository, CompoundMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a compound by its unique identifier.
     *
     * @param id the compound's UUID primary key
     * @return the matching compound response
     * @throws ResourceNotFoundException if no compound exists with this ID
     */
    @Transactional(readOnly = true)
    public CompoundResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compound", id)));
    }

    /**
     * Lists all compounds with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of compound DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<CompoundResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new compound.
     *
     * @param request the create request with compound details
     * @return the created compound response
     */
    @Transactional
    public CompoundResponse create(CreateCompoundRequest request) {
        Compound compound = mapper.toEntity(request);
        compound = repository.save(compound);
        log.info("Compound created: {}", compound.getCompoundName());
        return mapper.toResponse(compound);
    }

    /**
     * Updates an existing compound.
     *
     * @param id      the compound's UUID
     * @param request the update request with new values
     * @return the updated compound response
     * @throws ResourceNotFoundException if no compound exists with this ID
     */
    @Transactional
    public CompoundResponse update(UUID id, UpdateCompoundRequest request) {
        Compound compound = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compound", id));
        compound.setCompoundName(request.compoundName());
        compound.setCountry(request.country());
        compound.setCity(request.city());
        compound.setDistrict(request.district());
        compound = repository.save(compound);
        log.info("Compound updated: {}", compound.getCompoundName());
        return mapper.toResponse(compound);
    }

    /**
     * Deletes a compound by ID.
     *
     * @param id the compound's UUID
     * @throws ResourceNotFoundException if no compound exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Compound compound = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compound", id));
        repository.delete(compound);
        log.info("Compound deleted: {}", compound.getCompoundName());
    }
}
