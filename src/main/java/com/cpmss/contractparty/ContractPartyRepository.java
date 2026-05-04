package com.cpmss.contractparty;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link ContractParty} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface ContractPartyRepository
        extends JpaRepository<ContractParty, ContractPartyId> {
}
