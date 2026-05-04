package com.cpmss.hireagreement;

import com.cpmss.application.ApplicationId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link HireAgreement} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface HireAgreementRepository extends JpaRepository<HireAgreement, ApplicationId> {
}
