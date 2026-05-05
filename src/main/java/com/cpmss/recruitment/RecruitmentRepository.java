package com.cpmss.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link Recruitment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query
 * methods for listing interviews by application composite key.
 */
public interface RecruitmentRepository extends JpaRepository<Recruitment, RecruitmentId> {

    /**
     * Finds all interviews for a given application (composite FK).
     *
     * @param applicantId     the applicant's person UUID
     * @param positionId      the position UUID
     * @param applicationDate the application date
     * @return all interview records for that application
     */
    List<Recruitment> findByApplicantIdAndPositionIdAndApplicationDate(
            UUID applicantId, UUID positionId, LocalDate applicationDate);
}
