package com.cpmss.maintenance.personworksforcompany;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link PersonWorksForCompany} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PersonWorksForCompanyRepository
        extends JpaRepository<PersonWorksForCompany, PersonWorksForCompanyId> {
}
