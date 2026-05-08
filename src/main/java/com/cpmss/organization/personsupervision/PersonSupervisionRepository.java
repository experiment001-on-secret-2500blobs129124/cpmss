package com.cpmss.organization.personsupervision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link PersonSupervision} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and query methods
 * for supervision relationship lookups.
 */
public interface PersonSupervisionRepository
        extends JpaRepository<PersonSupervision, PersonSupervisionId> {

    /**
     * Finds all supervision records where the given person is the supervisor.
     *
     * @param supervisorId the supervisor's UUID
     * @return all supervisee records under this supervisor
     */
    List<PersonSupervision> findBySupervisorId(UUID supervisorId);

    /**
     * Finds all supervision records where the given person is the supervisee.
     *
     * @param superviseeId the supervisee's UUID
     * @return all supervisor records for this supervisee
     */
    List<PersonSupervision> findBySuperviseeId(UUID superviseeId);
}
