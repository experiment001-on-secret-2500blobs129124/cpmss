package com.cpmss.bankaccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link BankAccount} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
}
