package com.cpmss.personinvestsincompound;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Composite key for the {@link PersonInvestsInCompound} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonInvestsInCompoundId implements Serializable {

    /** The investing person. */
    private UUID investor;

    /** The compound being invested in. */
    private UUID compound;

    /** The timestamp of the investment event. */
    private Instant investedAt;
}
