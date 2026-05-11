package com.cpmss.finance.personinvestsincompound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link PersonInvestsInCompound} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface PersonInvestsInCompoundRepository
        extends JpaRepository<PersonInvestsInCompound, PersonInvestsInCompoundId> {

    /**
     * Lists all investment stakes, newest first.
     *
     * @return investment stakes ordered by investment time descending
     */
    List<PersonInvestsInCompound> findAllByOrderByInvestedAtDesc();

    /**
     * Lists investment stakes for one investor, newest first.
     *
     * @param investorId the investing person UUID
     * @return investment stakes ordered by investment time descending
     */
    List<PersonInvestsInCompound> findByInvestorIdOrderByInvestedAtDesc(UUID investorId);
}
