package com.cpmss.organization.personsupervision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link PersonSupervision} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and active relationship lookup
 * helpers used by department and team-scoped workflows.
 */
public interface PersonSupervisionRepository
        extends JpaRepository<PersonSupervision, PersonSupervisionId> {

    /**
     * Finds all supervision rows where the person is supervisor.
     *
     * @param supervisorId supervisor person UUID
     * @return supervision rows
     */
    List<PersonSupervision> findBySupervisorId(UUID supervisorId);

    /**
     * Finds active supervisees for a supervisor.
     *
     * @param supervisorId supervisor person UUID
     * @return active supervision rows
     */
    List<PersonSupervision> findBySupervisorIdAndSupervisionEndDateIsNull(UUID supervisorId);

    /**
     * Finds active supervisors for a supervisee.
     *
     * @param superviseeId supervisee person UUID
     * @return active supervision rows
     */
    List<PersonSupervision> findBySuperviseeIdAndSupervisionEndDateIsNull(UUID superviseeId);

    /**
     * Finds a specific supervision row by its natural composite fields.
     *
     * @param supervisorId         supervisor person UUID
     * @param superviseeId         supervisee person UUID
     * @param supervisionStartDate start date
     * @return the supervision row, if present
     */
    Optional<PersonSupervision> findBySupervisorIdAndSuperviseeIdAndSupervisionStartDate(
            UUID supervisorId, UUID superviseeId, LocalDate supervisionStartDate);
}
