package com.cpmss.installmentpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link InstallmentPayment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface InstallmentPaymentRepository extends JpaRepository<InstallmentPayment, UUID> {
}
