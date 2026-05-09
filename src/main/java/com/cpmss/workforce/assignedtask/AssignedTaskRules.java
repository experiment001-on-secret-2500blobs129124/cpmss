package com.cpmss.workforce.assignedtask;

import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @throws ApiException if a duplicate assignment exists
     */
    public void validateNoDuplicateAssignment(UUID staffId, UUID taskId,
                                               LocalDate assignmentDate, boolean exists) {
        if (exists) {
            throw new ApiException(WorkforceErrorCode.ASSIGNED_TASK_DUPLICATE);
        }
    }
}
