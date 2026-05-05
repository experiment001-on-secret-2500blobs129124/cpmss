package com.cpmss.personresidesunder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link PersonResidesUnder} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * listing all residents for a given contract.
 */
public interface PersonResidesUnderRepository
        extends JpaRepository<PersonResidesUnder, PersonResidesUnderId> {

    /**
     * Find all residents associated with a contract.
     *
     * @param contractId the contract's UUID
     * @return all residency records for that contract
     */
    List<PersonResidesUnder> findByContractId(UUID contractId);
}
