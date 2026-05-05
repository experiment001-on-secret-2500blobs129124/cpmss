package com.cpmss.contractparty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link ContractParty} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Custom queries support
 * listing all parties for a given contract.
 */
public interface ContractPartyRepository
        extends JpaRepository<ContractParty, ContractPartyId> {

    /**
     * Find all parties associated with a contract.
     *
     * @param contractId the contract's UUID
     * @return all contract party records for that contract
     */
    List<ContractParty> findByContractId(UUID contractId);
}
