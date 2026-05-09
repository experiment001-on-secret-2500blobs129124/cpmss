package com.cpmss.property.unit;

import com.cpmss.identity.auth.CurrentUserService;
import com.cpmss.property.common.PropertyAccessRules;
import com.cpmss.property.building.Building;
import com.cpmss.property.building.BuildingRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.common.PropertyErrorCode;
import com.cpmss.property.unit.dto.CreateUnitRequest;
import com.cpmss.property.unit.dto.UnitResponse;
import com.cpmss.property.unit.dto.UpdateUnitRequest;
import com.cpmss.property.unitpricinghistory.UnitPricingHistory;
import com.cpmss.property.unitpricinghistory.UnitPricingHistoryRepository;
import com.cpmss.property.unitpricinghistory.dto.CreateUnitPricingHistoryRequest;
import com.cpmss.property.unitpricinghistory.dto.UnitPricingHistoryResponse;
import com.cpmss.property.unitstatushistory.UnitStatusHistory;
import com.cpmss.property.unitstatushistory.UnitStatusHistoryRepository;
import com.cpmss.property.unitstatushistory.dto.CreateUnitStatusHistoryRequest;
import com.cpmss.property.unitstatushistory.dto.UnitStatusHistoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Orchestrates unit lifecycle operations.
 *
 * <p>Units are owned by a {@link Building}. Unit number uniqueness
 * within a building is enforced by {@link UnitRules}.
 * Also manages SCD Type 2 pricing and status history sub-resources.
 *
 * @see UnitRules
 * @see UnitRepository
 */
@Service
public class UnitService {

    private static final Logger log = LoggerFactory.getLogger(UnitService.class);

    private final UnitRepository repository;
    private final BuildingRepository buildingRepository;
    private final UnitPricingHistoryRepository pricingHistoryRepository;
    private final UnitStatusHistoryRepository statusHistoryRepository;
    private final UnitMapper mapper;
    private final CurrentUserService currentUserService;
    private final PropertyAccessRules accessRules = new PropertyAccessRules();
    private final UnitRules rules = new UnitRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository               unit data access
     * @param buildingRepository       building data access (for FK lookup)
     * @param pricingHistoryRepository pricing history data access
     * @param statusHistoryRepository  status history data access
     * @param mapper                   entity-DTO mapper
     */
    public UnitService(UnitRepository repository,
                       BuildingRepository buildingRepository,
                       UnitPricingHistoryRepository pricingHistoryRepository,
                       UnitStatusHistoryRepository statusHistoryRepository,
                       UnitMapper mapper,
                       CurrentUserService currentUserService) {
        this.repository = repository;
        this.buildingRepository = buildingRepository;
        this.pricingHistoryRepository = pricingHistoryRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Retrieves a unit by its unique identifier.
     *
     * @param id the unit's UUID primary key
     * @return the matching unit response
     * @throws ApiException if no unit exists with this ID
     */
    @Transactional(readOnly = true)
    public UnitResponse getById(UUID id) {
        accessRules.requirePropertyReader(currentUserService.currentUser());
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND)));
    }

    /**
     * Lists all units with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a paged response of unit DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<UnitResponse> listAll(Pageable pageable) {
        accessRules.requirePropertyReader(currentUserService.currentUser());
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new unit under the specified building.
     *
     * @param request the create request with unit details and building ID
     * @return the created unit response
     * @throws ApiException if the building does not exist
     */
    @Transactional
    public UnitResponse create(CreateUnitRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));

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
     * @throws ApiException if the unit or building does not exist
     */
    @Transactional
    public UnitResponse update(UUID id, UpdateUnitRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));

        Building building = buildingRepository.findById(request.buildingId())
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));

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
     * @throws ApiException if no unit exists with this ID
     */
    @Transactional
    public void delete(UUID id) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Unit unit = repository.findById(id)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));
        repository.delete(unit);
        log.info("Unit deleted: {}", unit.getUnitNo());
    }

    // ── Pricing History Sub-Resource ─────────────────────────────────────

    /**
     * Adds a pricing history entry to a unit.
     *
     * @param unitId  the unit's UUID
     * @param request the pricing details (effective date + listing price)
     * @return the created pricing history response
     * @throws ApiException if the unit does not exist
     */
    @Transactional
    public UnitPricingHistoryResponse addPricingHistory(UUID unitId,
                                                        CreateUnitPricingHistoryRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Unit unit = repository.findById(unitId)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));

        UnitPricingHistory history = new UnitPricingHistory();
        history.setUnit(unit);
        history.setEffectiveDate(request.effectiveDate());
        history.setListingPrice(request.listingPrice());
        history = pricingHistoryRepository.save(history);
        log.info("Pricing history added: unit={}, price={}, effective={}",
                unitId, request.listingPrice(), request.effectiveDate());
        return new UnitPricingHistoryResponse(
                unit.getId(), history.getEffectiveDate(), history.getListingPrice());
    }

    /**
     * Retrieves all pricing history entries for a unit.
     *
     * @param unitId the unit's UUID
     * @return pricing history entries, most recent first
     * @throws ApiException if the unit does not exist
     */
    @Transactional(readOnly = true)
    public List<UnitPricingHistoryResponse> getPricingHistory(UUID unitId) {
        accessRules.requirePropertyReader(currentUserService.currentUser());
        if (!repository.existsById(unitId)) {
            throw new ApiException(PropertyErrorCode.UNIT_NOT_FOUND);
        }
        return pricingHistoryRepository.findByUnitIdOrderByEffectiveDateDesc(unitId)
                .stream()
                .map(h -> new UnitPricingHistoryResponse(
                        h.getUnit().getId(), h.getEffectiveDate(), h.getListingPrice()))
                .toList();
    }

    // ── Status History Sub-Resource ──────────────────────────────────────

    /**
     * Adds a status history entry to a unit.
     *
     * @param unitId  the unit's UUID
     * @param request the status details (effective date + status)
     * @return the created status history response
     * @throws ApiException if the unit does not exist
     */
    @Transactional
    public UnitStatusHistoryResponse addStatusHistory(UUID unitId,
                                                      CreateUnitStatusHistoryRequest request) {
        accessRules.requirePropertyAdministrator(currentUserService.currentUser());
        Unit unit = repository.findById(unitId)
                .orElseThrow(() -> new ApiException(PropertyErrorCode.UNIT_NOT_FOUND));

        UnitStatusHistory history = new UnitStatusHistory();
        history.setUnit(unit);
        history.setEffectiveDate(request.effectiveDate());
        history.setUnitStatus(request.unitStatus());
        history = statusHistoryRepository.save(history);
        log.info("Status history added: unit={}, status={}, effective={}",
                unitId, request.unitStatus(), request.effectiveDate());
        return new UnitStatusHistoryResponse(
                unit.getId(), history.getEffectiveDate(), history.getUnitStatus());
    }

    /**
     * Retrieves all status history entries for a unit.
     *
     * @param unitId the unit's UUID
     * @return status history entries, most recent first
     * @throws ApiException if the unit does not exist
     */
    @Transactional(readOnly = true)
    public List<UnitStatusHistoryResponse> getStatusHistory(UUID unitId) {
        accessRules.requirePropertyReader(currentUserService.currentUser());
        if (!repository.existsById(unitId)) {
            throw new ApiException(PropertyErrorCode.UNIT_NOT_FOUND);
        }
        return statusHistoryRepository.findByUnitIdOrderByEffectiveDateDesc(unitId)
                .stream()
                .map(h -> new UnitStatusHistoryResponse(
                        h.getUnit().getId(), h.getEffectiveDate(), h.getUnitStatus()))
                .toList();
    }
}
