package com.cpmss.property.building;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.property.common.PropertyAccessRules;
import com.cpmss.property.building.dto.BuildingResponse;
import com.cpmss.property.building.dto.CreateBuildingRequest;
import com.cpmss.property.building.dto.UpdateBuildingRequest;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.property.common.PropertyErrorCode;
import com.cpmss.property.compound.Compound;
import com.cpmss.property.compound.CompoundRepository;
import com.cpmss.platform.exception.ApiException;
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
    private final CurrentUserService currentUserService;
    private final PropertyAccessRules accessRules = new PropertyAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         building data access
     * @param compoundRepository compound data access (for FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public BuildingService(BuildingRepository repository,
                           CompoundRepository compoundRepository,
                           BuildingMapper mapper,
                           CurrentUserService currentUserService) {
        this.repository = repository;
        this.compoundRepository = compoundRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a building by its unique identifier.
     *
     * @param id the building's UUID primary key
     * @return the matching building response
     * @throws ApiException if no building exists with this ID
     */
    @Transactional(readOnly = true)
    public BuildingResponse getById(UUID id) {
        accessRules.requirePropertyReader(currentUserService.currentUser());
        Building building = repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.BUILDING_NOT_FOUND));
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
        accessRules.requirePropertyReader(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new building under the specified compound.
     *
     * @param request the create request with building details and compound ID
     * @return the created building response
     * @throws ApiException if the compound does not exist
     */
    @Transactional
    public BuildingResponse create(CreateBuildingRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.COMPOUND_NOT_FOUND));

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
     * @throws ApiException if the building or compound does not exist
     */
    @Transactional
    public BuildingResponse update(UUID id, UpdateBuildingRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Building building = repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.BUILDING_NOT_FOUND));

        Compound compound = compoundRepository.findById(request.compoundId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.COMPOUND_NOT_FOUND));

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
     * @throws ApiException if no building exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Building building = repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.BUILDING_NOT_FOUND));
        repository.delete(building);
        log.info("Building deleted: {}", building.getBuildingNo());
    }
}
