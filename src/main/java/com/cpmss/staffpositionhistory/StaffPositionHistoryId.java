package com.cpmss.staffpositionhistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link StaffPositionHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StaffPositionHistoryId implements Serializable {

    /** The staff member (person) holding the position. */
    private UUID person;

    /** The position being held. */
    private UUID position;

    /** The date this position assignment became effective. */
    private LocalDate effectiveDate;
}
