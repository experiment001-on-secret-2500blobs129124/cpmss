package com.cpmss.workforce.taskmonthlysalary;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for the {@link TaskMonthlySalary} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskMonthlySalaryId implements Serializable {

    /** The staff member. */
    private UUID staff;

    /** The department. */
    private UUID department;

    /** The payroll year. */
    private Integer year;

    /** The payroll month. */
    private Integer month;
}
