package com.cpmss.property.unitstatushistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link UnitStatusHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UnitStatusHistoryId implements Serializable {

    /** The unit whose status is tracked. */
    private UUID unit;

    /** The date this status became effective. */
    private LocalDate effectiveDate;
}
