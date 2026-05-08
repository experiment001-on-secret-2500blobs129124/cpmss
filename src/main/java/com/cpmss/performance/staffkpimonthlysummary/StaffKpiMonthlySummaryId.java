package com.cpmss.performance.staffkpimonthlysummary;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for the {@link StaffKpiMonthlySummary} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StaffKpiMonthlySummaryId implements Serializable {

    /** The staff member. */
    private UUID staff;

    /** The department. */
    private UUID department;

    /** The summary year. */
    private Integer year;

    /** The summary month. */
    private Integer month;
}
