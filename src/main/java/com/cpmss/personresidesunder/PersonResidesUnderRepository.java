package com.cpmss.personresidesunder;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link PersonResidesUnder} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PersonResidesUnderRepository
        extends JpaRepository<PersonResidesUnder, PersonResidesUnderId> {
}
