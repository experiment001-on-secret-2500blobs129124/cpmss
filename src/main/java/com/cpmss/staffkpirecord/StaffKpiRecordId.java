package com.cpmss.staffkpirecord;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link StaffKpiRecord} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StaffKpiRecordId implements Serializable {

    /** The staff member. */
    private UUID staff;

    /** The department. */
    private UUID department;

    /** The date the KPI was recorded. */
    private LocalDate recordDate;
}
