package com.cpmss.personresidesunder;

import com.cpmss.common.BaseAuditEntity;
import com.cpmss.contract.Contract;
import com.cpmss.person.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Junction entity recording which persons physically reside in a unit under a contract.
 *
 * <p>Composite PK: ({@code resident_id}, {@code contract_id}, {@code move_in_date}).
 * The same person can vacate and return under the same contract.
 * The Primary Signer is NOT automatically a resident.
 */
@Entity
@Table(name = "Person_Resides_Under")
@IdClass(PersonResidesUnderId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonResidesUnder extends BaseAuditEntity {

    /** The resident person (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Person resident;

    /** The residential contract (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    /** The date the person moved in (part of composite PK). */
    @Id
    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    /** The date the person moved out ({@code null} = still residing). */
    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    /** Relationship to the primary contract holder. */
    @Column(name = "household_relationship", nullable = false, length = 50)
    private String householdRelationship;
}
