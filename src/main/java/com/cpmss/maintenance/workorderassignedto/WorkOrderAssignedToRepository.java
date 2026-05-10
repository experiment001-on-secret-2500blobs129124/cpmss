package com.cpmss.maintenance.workorderassignedto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link WorkOrderAssignedTo} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository} and assignment lookup helpers
 * used by the parent work-order workflow.
 */
public interface WorkOrderAssignedToRepository
        extends JpaRepository<WorkOrderAssignedTo, WorkOrderAssignedToId> {

    /**
     * Checks whether a company is already linked to a work order.
     *
     * @param workOrderId the work order UUID
     * @param companyId   the company UUID
     * @return true when the assignment row exists
     */
    boolean existsByWorkOrderIdAndCompanyId(UUID workOrderId, UUID companyId);

    /**
     * Finds vendor assignments for a work order, newest first.
     *
     * @param workOrderId the work order UUID
     * @return work-order vendor assignments
     */
    List<WorkOrderAssignedTo> findByWorkOrderIdOrderByDateAssignedDesc(UUID workOrderId);
}
