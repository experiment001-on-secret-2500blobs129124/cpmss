package com.cpmss.security.gateguardassignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data repository for {@link GateGuardAssignment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and assignment checks used by
 * gate-entry ownership rules.
 */
public interface GateGuardAssignmentRepository
        extends JpaRepository<GateGuardAssignment, UUID> {

    /**
     * Checks whether a guard is posted at a gate for the event timestamp.
     *
     * <p>An assignment is active when the event is on or after
     * {@code shift_start} and before {@code shift_end}; a null shift end means
     * the guard is still on duty.
     *
     * @param guardId person UUID of the guard
     * @param gateId  gate UUID being accessed
     * @param at      timestamp of the gate event
     * @return true when the guard has an active posting at the gate
     */
    @Query("""
            select count(assignment) > 0
            from GateGuardAssignment assignment
            where assignment.guard.id = :guardId
              and assignment.gate.id = :gateId
              and assignment.shiftStart <= :at
              and (assignment.shiftEnd is null or assignment.shiftEnd >= :at)
            """)
    boolean existsActivePostingAtGate(@Param("guardId") UUID guardId,
                                      @Param("gateId") UUID gateId,
                                      @Param("at") Instant at);

    /**
     * Finds active assignments for a specific guard.
     *
     * @param guardId person UUID of the guard
     * @param at      timestamp used to determine active assignments
     * @param pageable pagination parameters
     * @return active guard assignments for the guard
     */
    @Query("""
            select assignment
            from GateGuardAssignment assignment
            where assignment.guard.id = :guardId
              and assignment.shiftStart <= :at
              and (assignment.shiftEnd is null or assignment.shiftEnd >= :at)
            """)
    Page<GateGuardAssignment> findActiveByGuardId(@Param("guardId") UUID guardId,
                                                  @Param("at") Instant at,
                                                  Pageable pageable);
}
