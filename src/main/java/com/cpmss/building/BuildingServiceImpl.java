package com.cpmss.building;

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
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    @Override
    public Optional<Building> findById(UUID id) {
        return buildingRepository.findById(id);
    }

    @Override
    @Transactional
    public Building create(Building building) {
        return buildingRepository.save(building);
    }

    @Override
    @Transactional
    public Building update(UUID id, Building updated) {
        Building existing = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found: " + id));
        existing.setBuildingName(updated.getBuildingName());
        existing.setBuildingNumber(updated.getBuildingNumber());
        existing.setBuildingType(updated.getBuildingType());
        existing.setFloorsCount(updated.getFloorsCount());
        existing.setConstructionDate(updated.getConstructionDate());
        return buildingRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!buildingRepository.existsById(id)) {
            throw new EntityNotFoundException("Building not found: " + id);
        }
        buildingRepository.deleteById(id);
    }

    @Override
    public long count() {
        return buildingRepository.count();
    }
}
