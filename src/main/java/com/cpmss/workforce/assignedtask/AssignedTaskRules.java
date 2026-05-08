package com.cpmss.workforce.assignedtask;

import com.cpmss.platform.exception.ConflictException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Business rules for {@link AssignedTask} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see AssignedTaskService
 */
public class AssignedTaskRules {

    /**
     * Validates that the same staff member is not assigned the same
     * task on the same date twice.
     *
     * @param staffId        the staff member UUID
     * @param taskId         the task UUID
     * @param assignmentDate the assignment date
     * @param exists         whether a matching assignment already exists
     * @throws ConflictException if a duplicate assignment exists
     */
    public void validateNoDuplicateAssignment(UUID staffId, UUID taskId,
                                               LocalDate assignmentDate, boolean exists) {
        if (exists) {
            throw new ConflictException(
                    "Staff " + staffId + " is already assigned task " + taskId
                            + " on " + assignmentDate);
        }
    }
}
