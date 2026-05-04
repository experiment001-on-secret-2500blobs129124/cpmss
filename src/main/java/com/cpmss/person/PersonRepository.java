package com.cpmss.person;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Person} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and custom query methods
 * for passport and national ID uniqueness checks.
 */
public interface PersonRepository extends JpaRepository<Person, UUID> {

    /**
     * Checks whether a person with the given passport number exists.
     *
     * @param passportNo the passport number to check
     * @return true if a matching person exists
     */
    boolean existsByPassportNo(String passportNo);

    /**
     * Checks whether a person with the given Egyptian national ID exists.
     *
     * @param egyptianNationalId the 14-digit national ID to check
     * @return true if a matching person exists
     */
    boolean existsByEgyptianNationalId(String egyptianNationalId);
}
