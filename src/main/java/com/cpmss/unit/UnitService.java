package com.cpmss.unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitService {
    List<Unit> findAll();
    Optional<Unit> findById(UUID id);
    List<Unit> findByBuilding(UUID buildingId);
    List<Unit> findVacant();
    Unit create(Unit unit);
    Unit update(UUID id, Unit unit);
    void delete(UUID id);
    long count();
    long countByStatus(String status);
}
