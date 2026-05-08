package com.cpmss.property.facilityhourshistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link FacilityHoursHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for SCD Type 2 operating hours lookups.
 */
public interface FacilityHoursHistoryRepository
        extends JpaRepository<FacilityHoursHistory, FacilityHoursHistoryId> {

    /**
     * Finds all hours history entries for a facility, ordered by date descending.
     *
     * @param facilityId the facility's UUID
     * @return hours history entries, most recent first
     */
    List<FacilityHoursHistory> findByFacilityIdOrderByEffectiveDateDesc(UUID facilityId);
}
