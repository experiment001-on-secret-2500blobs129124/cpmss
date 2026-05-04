package com.cpmss.contractparty;

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

    /** The person's role in the contract. */
    private String role;
}
