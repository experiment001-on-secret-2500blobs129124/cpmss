package com.cpmss.building;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingService {
    List<Building> findAll();
    Optional<Building> findById(UUID id);
    Building create(Building building);
    Building update(UUID id, Building building);
    void delete(UUID id);
    long count();
}
