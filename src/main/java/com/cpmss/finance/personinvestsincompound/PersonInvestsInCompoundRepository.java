package com.cpmss.finance.personinvestsincompound;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link PersonInvestsInCompound} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PersonInvestsInCompoundRepository
        extends JpaRepository<PersonInvestsInCompound, PersonInvestsInCompoundId> {
}
