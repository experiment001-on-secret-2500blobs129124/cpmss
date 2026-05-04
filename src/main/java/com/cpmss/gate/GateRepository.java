package com.cpmss.gate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Gate} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Includes a uniqueness
 * check for gate number (system-wide unique constraint).
 */
public interface GateRepository extends JpaRepository<Gate, UUID> {

    /**
     * Checks whether a gate with the given gate number exists.
     *
     * @param gateNo the gate number to check
     * @return true if a matching gate exists
     */
    boolean existsByGateNo(String gateNo);
}
