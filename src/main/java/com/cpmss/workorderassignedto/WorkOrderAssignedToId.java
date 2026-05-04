package com.cpmss.workorderassignedto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for the {@link WorkOrderAssignedTo} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkOrderAssignedToId implements Serializable {

    /** The work order. */
    private UUID workOrder;

    /** The assigned company. */
    private UUID company;
}
