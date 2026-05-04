package com.cpmss.staffsalaryhistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link StaffSalaryHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StaffSalaryHistoryId implements Serializable {

    /** The staff member. */
    private UUID staff;

    /** The date this rate became effective. */
    private LocalDate effectiveDate;
}
