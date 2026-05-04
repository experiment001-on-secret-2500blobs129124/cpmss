package com.cpmss.unit;

import com.cpmss.building.Building;
import com.cpmss.building.BuildingRepository;
import com.cpmss.common.PagedResponse;
import com.cpmss.exception.ResourceNotFoundException;
import com.cpmss.unit.dto.CreateUnitRequest;
import com.cpmss.unit.dto.UnitResponse;
import com.cpmss.unit.dto.UpdateUnitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates unit lifecycle operations.
 *
 * <p>Units are owned by a {@link Building}. Unit number uniqueness
 * within a building is enforced by {@link UnitRules}.
 *
 * @see UnitRules
 * @see UnitRepository
 */
@Service
public class UnitService {

    private static final Logger log = LoggerFactory.getLogger(UnitService.class);

    private final UnitRepository repository;
    private final BuildingRepository buildingRepository;
    private final UnitMapper mapper;
    private final UnitRules rules = new UnitRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         unit data access
     * @param buildingRepository building data access (for FK lookup)
     * @param mapper             entity-DTO mapper
     */
    public UnitService(UnitRepository repository,
                       BuildingRepository buildingRepository,
                       UnitMapper mapper) {
        this.repository = repository;
        this.buildingRepository = buildingRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a unit by its unique identifier.
     *
     * @param id the unit's UUID primary key
     * @return the matching unit response
     * @throws ResourceNotFoundException if no unit exists with this ID
     */
    @Transactional(readOnly = true)
    public UnitResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", id)));
    }

    /**
     * Lists all units with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of unit DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<UnitResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new unit under the specified building.
     *
     * @param request the create request with unit details and building ID
     * @return the created unit response
     * @throws ResourceNotFoundException if the building does not exist
     */
    @Transactional
    public UnitResponse create(CreateUnitRequest request) {
        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", request.buildingId()));

        rules.validateUnitNoUniqueInBuilding(request.unitNo(),
                repository.existsByUnitNoAndBuildingId(request.unitNo(), request.buildingId()));

        Unit unit = Unit.builder()
                .unitNo(request.unitNo())
                .floorNo(request.floorNo())
                .noOfRooms(request.noOfRooms())
                .noOfBathrooms(request.noOfBathrooms())
                .noOfBedrooms(request.noOfBedrooms())
                .noOfTotalRooms(request.noOfTotalRooms())
                .noOfBalconies(request.noOfBalconies())
                .squareFoot(request.squareFoot())
                .viewOrientation(request.viewOrientation())
                .gasMeterCode(request.gasMeterCode())
                .waterMeterCode(request.waterMeterCode())
                .electricityMeterCode(request.electricityMeterCode())
                .building(building)
                .build();
        unit = repository.save(unit);
        log.info("Unit created: {} in building {}", unit.getUnitNo(), building.getBuildingNo());
        return mapper.toResponse(unit);
    }

    /**
     * Updates an existing unit.
     *
     * @param id      the unit's UUID
     * @param request the update request with new values
     * @return the updated unit response
     * @throws ResourceNotFoundException if the unit or building does not exist
     */
    @Transactional
    public UnitResponse update(UUID id, UpdateUnitRequest request) {
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", id));

        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building", request.buildingId()));

        boolean noChanged = !unit.getUnitNo().equals(request.unitNo());
        boolean bldgChanged = !unit.getBuilding().getId().equals(request.buildingId());
        if (noChanged || bldgChanged) {
            rules.validateUnitNoUniqueInBuilding(request.unitNo(),
                    repository.existsByUnitNoAndBuildingId(request.unitNo(), request.buildingId()));
        }

        unit.setUnitNo(request.unitNo());
        unit.setFloorNo(request.floorNo());
        unit.setNoOfRooms(request.noOfRooms());
        unit.setNoOfBathrooms(request.noOfBathrooms());
        unit.setNoOfBedrooms(request.noOfBedrooms());
        unit.setNoOfTotalRooms(request.noOfTotalRooms());
        unit.setNoOfBalconies(request.noOfBalconies());
        unit.setSquareFoot(request.squareFoot());
        unit.setViewOrientation(request.viewOrientation());
        unit.setGasMeterCode(request.gasMeterCode());
        unit.setWaterMeterCode(request.waterMeterCode());
        unit.setElectricityMeterCode(request.electricityMeterCode());
        unit.setBuilding(building);
        unit = repository.save(unit);
        log.info("Unit updated: {}", unit.getUnitNo());
        return mapper.toResponse(unit);
    }

    /**
     * Deletes a unit by ID.
     *
     * @param id the unit's UUID
     * @throws ResourceNotFoundException if no unit exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", id));
        repository.delete(unit);
        log.info("Unit deleted: {}", unit.getUnitNo());
    }
}
