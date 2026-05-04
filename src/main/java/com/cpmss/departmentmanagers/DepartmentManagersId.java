package com.cpmss.departmentmanagers;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link DepartmentManagers} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DepartmentManagersId implements Serializable {

    /** The department being managed. */
    private UUID department;

    /** The person acting as manager. */
    private UUID manager;

    /** The date this management assignment started. */
    private LocalDate managementStartDate;
}
