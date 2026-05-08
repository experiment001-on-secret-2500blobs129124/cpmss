package com.cpmss.workforce.attends;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link Attends} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AttendsId implements Serializable {

    /** The staff member. */
    private UUID staff;

    /** The shift type. */
    private UUID shift;

    /** The attendance date. */
    private LocalDate date;
}
