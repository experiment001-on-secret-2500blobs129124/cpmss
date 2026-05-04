package com.cpmss.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Vehicle} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Includes a uniqueness
 * check for the license plate number.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    /**
     * Checks whether a vehicle with the given license number exists.
     *
     * @param licenseNo the license plate number to check
     * @return true if a matching vehicle exists
     */
    boolean existsByLicenseNo(String licenseNo);
}
