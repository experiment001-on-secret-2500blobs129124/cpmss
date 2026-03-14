package com.cpmss.unit;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    @Override
    public Optional<Unit> findById(UUID id) {
        return unitRepository.findById(id);
    }

    @Override
    public List<Unit> findByBuilding(UUID buildingId) {
        return unitRepository.findByBuildingId(buildingId);
    }

    @Override
    public List<Unit> findVacant() {
        return unitRepository.findByCurrentStatus("Vacant");
    }

    @Override
    @Transactional
    public Unit create(Unit unit) {
        if (unit.getCurrentStatus() == null) {
            unit.setCurrentStatus("Vacant");
        }
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public Unit update(UUID id, Unit updated) {
        Unit existing = unitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found: " + id));
        existing.setUnitNumber(updated.getUnitNumber());
        existing.setFloorNumber(updated.getFloorNumber());
        existing.setBedrooms(updated.getBedrooms());
        existing.setBathrooms(updated.getBathrooms());
        existing.setRooms(updated.getRooms());
        existing.setSquareFootage(updated.getSquareFootage());
        existing.setBalconies(updated.getBalconies());
        existing.setViewOrientation(updated.getViewOrientation());
        existing.setListingPrice(updated.getListingPrice());
        existing.setWaterMeterCode(updated.getWaterMeterCode());
        existing.setGasMeterCode(updated.getGasMeterCode());
        existing.setElectricityMeterCode(updated.getElectricityMeterCode());
        return unitRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!unitRepository.existsById(id)) {
            throw new EntityNotFoundException("Unit not found: " + id);
        }
        unitRepository.deleteById(id);
    }

    @Override
    public long count() {
        return unitRepository.count();
    }

    @Override
    public long countByStatus(String status) {
        return unitRepository.countByCurrentStatus(status);
    }
}
