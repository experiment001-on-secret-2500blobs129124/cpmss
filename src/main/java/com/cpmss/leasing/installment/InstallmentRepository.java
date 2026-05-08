package com.cpmss.leasing.installment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Installment} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
}
