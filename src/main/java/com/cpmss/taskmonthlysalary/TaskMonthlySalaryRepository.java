package com.cpmss.taskmonthlysalary;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link TaskMonthlySalary} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface TaskMonthlySalaryRepository
        extends JpaRepository<TaskMonthlySalary, TaskMonthlySalaryId> {
}
