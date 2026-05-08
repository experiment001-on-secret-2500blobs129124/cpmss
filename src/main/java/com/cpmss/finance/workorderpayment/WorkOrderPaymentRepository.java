package com.cpmss.finance.workorderpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link WorkOrderPayment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface WorkOrderPaymentRepository extends JpaRepository<WorkOrderPayment, UUID> {
}
