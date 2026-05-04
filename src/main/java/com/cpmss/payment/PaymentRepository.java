package com.cpmss.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Payment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
