package com.cpmss.hr.staffposition;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link PositionSalaryHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PositionSalaryHistoryId implements Serializable {

    /** The staff position this salary band belongs to. */
    private UUID position;

    /** The date this salary band became effective. */
    private LocalDate salaryEffectiveDate;
}
