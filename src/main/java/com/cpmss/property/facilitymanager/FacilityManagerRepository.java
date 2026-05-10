package com.cpmss.property.facilitymanager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link FacilityManager} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for facility manager assignment lookups.
 */
public interface FacilityManagerRepository
        extends JpaRepository<FacilityManager, FacilityManagerId> {

    /**
     * Finds all manager assignments for a facility, ordered by start date descending.
     *
     * @param facilityId the facility's UUID
     * @return manager assignments, most recent first
     */
    List<FacilityManager> findByFacilityIdOrderByManagementStartDateDesc(UUID facilityId);

    /**
     * Finds the current manager assignment for a facility.
     *
     * @param facilityId the facility UUID
     * @return the active manager assignment, if present
     */
    Optional<FacilityManager> findFirstByFacilityIdAndManagementEndDateIsNullOrderByManagementStartDateDesc(
            UUID facilityId);
}
