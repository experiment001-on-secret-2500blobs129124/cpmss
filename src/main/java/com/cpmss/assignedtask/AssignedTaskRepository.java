package com.cpmss.assignedtask;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Spring Data repository for {@link AssignedTask} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Includes duplicate
 * assignment check for the unique constraint.
 */
public interface AssignedTaskRepository extends JpaRepository<AssignedTask, UUID> {

    /**
     * Checks whether a task assignment already exists for the given
     * staff member, task, and date combination.
     *
     * @param staffId        the staff member UUID
     * @param taskId         the task UUID
     * @param assignmentDate the assignment date
     * @return true if a matching assignment exists
     */
    boolean existsByStaffIdAndTaskIdAndAssignmentDate(UUID staffId, UUID taskId,
                                                      LocalDate assignmentDate);

    /**
     * Checks whether any task assignment exists for a staff member on a given date.
     *
     * @param staffId        the staff member UUID
     * @param assignmentDate the assignment date
     * @return true if any assignment exists for that day
     */
    boolean existsByStaffIdAndAssignmentDate(UUID staffId, LocalDate assignmentDate);
}
