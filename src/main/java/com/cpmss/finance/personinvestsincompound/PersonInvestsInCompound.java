package com.cpmss.finance.personinvestsincompound;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.property.compound.Compound;
import com.cpmss.people.person.Person;
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

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Junction entity recording investment stakes held by persons in compounds (M:M).
 *
 * <p>Composite PK: ({@code investor_id}, {@code compound_id}, {@code invested_at}).
 * Investment records are permanent financial history — never deleted.
 */
@Entity
@Table(name = "Person_Invests_in_Compound")
@IdClass(PersonInvestsInCompoundId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonInvestsInCompound extends BaseAuditEntity {

    /** The investing person (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Person investor;

    /** The compound being invested in (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id")
    private Compound compound;

    /** The timestamp of the investment event (part of composite PK). */
    @Id
    @Column(name = "invested_at")
    private Instant investedAt;

    /** Investment stake amount. */
    @Column(name = "stock", precision = 10, scale = 2)
    private BigDecimal stock;
}
