package com.cpmss.staffsalaryhistory;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link StaffSalaryHistory} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffSalaryHistoryRepository
        extends JpaRepository<StaffSalaryHistory, StaffSalaryHistoryId> {
}
