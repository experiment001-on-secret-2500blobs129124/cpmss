package com.cpmss.personsupervision;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link PersonSupervision} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonSupervisionId implements Serializable {

    /** The supervising person. */
    private UUID supervisor;

    /** The supervised person. */
    private UUID supervisee;

    /** The date this supervision relationship started. */
    private LocalDate supervisionStartDate;
}
