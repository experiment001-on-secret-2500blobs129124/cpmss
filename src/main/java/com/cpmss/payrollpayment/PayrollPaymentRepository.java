package com.cpmss.payrollpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link PayrollPayment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PayrollPaymentRepository extends JpaRepository<PayrollPayment, UUID> {
}
