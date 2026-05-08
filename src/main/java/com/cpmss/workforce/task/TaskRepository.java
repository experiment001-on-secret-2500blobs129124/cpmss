package com.cpmss.workforce.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Task} entities.
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * Checks whether a task with the given title exists in a department.
     *
     * @param taskTitle    the task title to check
     * @param departmentId the department UUID
     * @return true if a matching task exists
     */
    boolean existsByTaskTitleAndDepartmentId(String taskTitle, UUID departmentId);
}
