package com.cpmss.workorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link WorkOrder} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
}
