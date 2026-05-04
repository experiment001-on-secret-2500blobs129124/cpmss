package com.cpmss.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Recruitment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface RecruitmentRepository extends JpaRepository<Recruitment, RecruitmentId> {
}
