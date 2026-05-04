package com.cpmss.gateguardassignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link GateGuardAssignment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface GateGuardAssignmentRepository
        extends JpaRepository<GateGuardAssignment, UUID> {
}
