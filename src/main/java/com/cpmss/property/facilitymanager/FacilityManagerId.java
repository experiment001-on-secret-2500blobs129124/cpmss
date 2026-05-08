package com.cpmss.property.facilitymanager;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link FacilityManager} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FacilityManagerId implements Serializable {

    /** The facility being managed. */
    private UUID facility;

    /** The person acting as manager. */
    private UUID manager;

    /** The date this management assignment started. */
    private LocalDate managementStartDate;
}
