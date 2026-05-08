package com.cpmss.organization.department;

import com.cpmss.platform.exception.ConflictException;

/**
 * Business rules for {@link Department} operations.
 *
 * <p>Stateless — no repositories injected. The service loads all
 * necessary data and passes it to these validation methods.
 *
 * @see DepartmentService
 */
public class DepartmentRules {

    /**
     * Validates that a department name is not already taken.
     *
     * @param name   the desired department name
     * @param exists whether a department with this name already exists
     * @throws ConflictException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ConflictException("Department '" + name + "' already exists");
        }
    }
}
