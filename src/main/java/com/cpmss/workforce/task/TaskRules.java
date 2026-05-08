package com.cpmss.workforce.task;

import com.cpmss.platform.exception.ConflictException;

/**
 * Business rules for {@link Task} operations.
 *
 * @see TaskService
 */
public class TaskRules {

    /**
     * Validates that a task title is unique within a department.
     *
     * @param title  the desired task title
     * @param exists whether a task with this title already exists in the department
     * @throws ConflictException if the title is already in use
     */
    public void validateTitleUniqueInDepartment(String title, boolean exists) {
        if (exists) {
            throw new ConflictException("Task '" + title + "' already exists in this department");
        }
    }
}
