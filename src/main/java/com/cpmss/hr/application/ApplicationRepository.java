package com.cpmss.hr.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link Application} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for listing applications by applicant.
 */
public interface ApplicationRepository extends JpaRepository<Application, ApplicationId> {

    /**
     * Finds all applications for a given applicant, ordered by date descending.
     *
     * @param applicantId the applicant's person UUID
     * @return applications for that person, most recent first
     */
    List<Application> findByApplicantIdOrderByApplicationDateDesc(UUID applicantId);

    /**
     * Checks whether an application already exists for the same composite key.
     *
     * @param applicantId the applicant UUID
     * @param positionId the position UUID
     * @param applicationDate the application date
     * @return true when the application already exists
     */
    boolean existsByApplicantIdAndPositionIdAndApplicationDate(
            UUID applicantId, UUID positionId, LocalDate applicationDate);
}
