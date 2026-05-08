package com.cpmss.organization.departmentlocationhistory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link DepartmentLocationHistory} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DepartmentLocationHistoryId implements Serializable {

    /** The department whose location is tracked. */
    private UUID department;

    /** The date this location assignment became effective. */
    private LocalDate locationStartDateInBuilding;
}
