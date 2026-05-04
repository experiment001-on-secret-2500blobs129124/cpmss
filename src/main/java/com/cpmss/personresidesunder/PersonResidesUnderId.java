package com.cpmss.personresidesunder;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link PersonResidesUnder} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonResidesUnderId implements Serializable {

    /** The resident person. */
    private UUID resident;

    /** The residential contract. */
    private UUID contract;

    /** The date the person moved in. */
    private LocalDate moveInDate;
}
