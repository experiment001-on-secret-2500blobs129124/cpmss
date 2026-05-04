package com.cpmss.building;

import com.cpmss.building.dto.BuildingResponse;
import com.cpmss.building.dto.CreateBuildingRequest;
import com.cpmss.building.dto.UpdateBuildingRequest;
import com.cpmss.common.PagedResponse;
import com.cpmss.compound.Compound;
import com.cpmss.compound.CompoundRepository;
import com.cpmss.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates building lifecycle operations.
 *
 * <p>Buildings are owned by a {@link Compound} — the compound ID
 * is required on every create and update. No business rules beyond
 * FK validation are needed.
 *
 * @see BuildingRepository
 */
@Service
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository repository;
    private final CompoundRepository compoundRepository;
    private final BuildingMapper mapper;

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         building data access
     * @param compoundRepository compound data access (for FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public BuildingService(BuildingRepository repository,
                           CompoundRepository compoundRepository,
                           BuildingMapper mapper) {
        this.repository = repository;
        this.compoundRepository = compoundRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a building by its unique identifier.
     *
     * @param id the building's UUID primary key
     * @return the matching building response
     * @throws ResourceNotFoundException if no building exists with this ID
     */
    @Transactional(readOnly = true)
    public BuildingResponse getById(UUID id) {
        Building building = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));
        return mapper.toResponse(building);
    }

    /**
     * Lists all buildings with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of building DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<BuildingResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new building under the specified compound.
     *
     * @param request the create request with building details and compound ID
     * @return the created building response
     * @throws ResourceNotFoundException if the compound does not exist
     */
    @Transactional
    public BuildingResponse create(CreateBuildingRequest request) {
        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Compound", request.compoundId()));

        Building building = Building.builder()
                .buildingNo(request.buildingNo())
                .buildingName(request.buildingName())
                .buildingType(request.buildingType())
                .floorsCount(request.floorsCount())
                .constructionDate(request.constructionDate())
                .compound(compound)
                .build();
        building = repository.save(building);
        log.info("Building created: {} in {}", building.getBuildingNo(), compound.getCompoundName());
        return mapper.toResponse(building);
    }

    /**
     * Updates an existing building.
     *
     * @param id      the building's UUID
     * @param request the update request with new values
     * @return the updated building response
     * @throws ResourceNotFoundException if the building or compound does not exist
     */
    @Transactional
    public BuildingResponse update(UUID id, UpdateBuildingRequest request) {
        Building building = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));

        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Compound", request.compoundId()));

        building.setBuildingNo(request.buildingNo());
        building.setBuildingName(request.buildingName());
        building.setBuildingType(request.buildingType());
        building.setFloorsCount(request.floorsCount());
        building.setConstructionDate(request.constructionDate());
        building.setCompound(compound);
        building = repository.save(building);
        log.info("Building updated: {}", building.getBuildingNo());
        return mapper.toResponse(building);
    }

    /**
     * Deletes a building by ID.
     *
     * @param id the building's UUID
     * @throws ResourceNotFoundException if no building exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Building building = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));
        repository.delete(building);
        log.info("Building deleted: {}", building.getBuildingNo());
    }
}
