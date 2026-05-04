package com.cpmss.application;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Application} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface ApplicationRepository extends JpaRepository<Application, ApplicationId> {
}
