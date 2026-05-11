package com.cpmss.leasing.contractparty;

import com.cpmss.leasing.common.ContractPartyRole;
import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.leasing.contract.Contract;
import com.cpmss.people.person.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Junction entity recording every party involved in a contract with their role.
 *
 * <p>Composite PK: ({@code person_id}, {@code contract_id}, {@code role}).
 * Valid roles: Primary Signer, Guarantor, Emergency Contact,
 * Corporate Representative, Authorizing Staff. Each contract may have
 * at most one Primary Signer — enforced by partial unique index in V2.
 */
@Entity
@Table(name = "Contract_Party")
@IdClass(ContractPartyId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractParty extends BaseAuditEntity {

    /** The person involved in the contract (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /** The contract (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /** The person's role label in the contract (part of composite PK). */
    @Id
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    /**
     * Returns the party role as a domain enum while storing the schema label.
     *
     * @return the contract party role, or {@code null} when not assigned
     */
    public ContractPartyRole getRole() {
        return role != null ? ContractPartyRole.fromLabel(role) : null;
    }

    /**
     * Stores the party role using the exact Flyway/database label.
     *
     * @param role the contract party role
     */
    public void setRole(ContractPartyRole role) {
        this.role = role != null ? role.label() : null;
    }

    /** Date and time the party signed the contract ({@code null} = not yet signed). */
    @Column(name = "date_signed")
    private Instant dateSigned;
}
