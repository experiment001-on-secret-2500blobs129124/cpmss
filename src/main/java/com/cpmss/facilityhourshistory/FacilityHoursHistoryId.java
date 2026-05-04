package com.cpmss.facilityhourshistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link FacilityHoursHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FacilityHoursHistoryId implements Serializable {

    /** The facility whose hours are tracked. */
    private UUID facility;

    /** The date this hours schedule became effective. */
    private LocalDate effectiveDate;
}
