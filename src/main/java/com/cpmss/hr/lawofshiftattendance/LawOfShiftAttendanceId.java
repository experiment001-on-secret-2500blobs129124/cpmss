package com.cpmss.hr.lawofshiftattendance;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link LawOfShiftAttendance} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LawOfShiftAttendanceId implements Serializable {

    /** The shift type this rule applies to. */
    private UUID shift;

    /** The date this rule set became effective. */
    private LocalDate effectiveDate;
}
