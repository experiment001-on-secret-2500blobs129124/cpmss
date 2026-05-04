package com.cpmss.workorderassignedto;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link WorkOrderAssignedTo} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface WorkOrderAssignedToRepository
        extends JpaRepository<WorkOrderAssignedTo, WorkOrderAssignedToId> {
}
