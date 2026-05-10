package com.cpmss.finance.bankaccount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link BankAccount} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    /**
     * Finds bank accounts owned by one person.
     *
     * @param accountOwnerId person UUID that owns the bank account
     * @param pageable       pagination parameters
     * @return page of bank accounts for that owner
     */
    Page<BankAccount> findByAccountOwnerId(UUID accountOwnerId, Pageable pageable);
}
