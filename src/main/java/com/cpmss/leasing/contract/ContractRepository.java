package com.cpmss.leasing.contract;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Contract} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Includes uniqueness
 * check for the contract reference number.
 */
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    /**
     * Checks whether a contract with the given reference exists.
     *
     * @param contractReference the contract reference to check
     * @return true if a matching contract exists
     */
    boolean existsByContractReference(String contractReference);
}
