package com.cpmss.leasing.contractparty;

import com.cpmss.leasing.common.ContractPartyRole;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for the {@link ContractParty} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContractPartyId implements Serializable {

    /** The person involved in the contract. */
    private UUID person;

    /** The contract. */
    private UUID contract;

    /** The person's role label in the contract. */
    private String role;

    /**
     * Creates a contract-party key from a domain role.
     *
     * @param person the person UUID
     * @param contract the contract UUID
     * @param role the contract party role
     */
    public ContractPartyId(UUID person, UUID contract, ContractPartyRole role) {
        this.person = person;
        this.contract = contract;
        this.role = role != null ? role.label() : null;
    }
}
