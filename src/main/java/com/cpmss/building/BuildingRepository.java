package com.cpmss.building;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<Building, UUID> {

    List<Building> findByCompoundId(UUID compoundId);

    List<Building> findByBuildingType(String buildingType);
}
