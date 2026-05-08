package com.cpmss.people.role;

import com.cpmss.platform.exception.ConflictException;

/**
 * Business rules for {@link Role} operations.
 *
 * @see RoleService
 */
public class RoleRules {

    /**
     * Validates that a role name is not already taken.
     *
     * @param name   the desired role name
     * @param exists whether a role with this name already exists
     * @throws ConflictException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ConflictException("Role '" + name + "' already exists");
        }
    }
}
