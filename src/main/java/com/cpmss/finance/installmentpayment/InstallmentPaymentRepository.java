package com.cpmss.finance.installmentpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link InstallmentPayment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface InstallmentPaymentRepository extends JpaRepository<InstallmentPayment, UUID> {

    /**
     * Checks whether a child payment row already exists for the parent payment.
     *
     * @param paymentId the parent payment UUID
     * @return true when the detail row exists
     */
    boolean existsByPaymentId(java.util.UUID paymentId);
}
