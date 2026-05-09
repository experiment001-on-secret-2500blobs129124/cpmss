package com.cpmss.people.role;

import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @throws ApiException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ApiException(PeopleErrorCode.ROLE_DUPLICATE);
        }
    }
}
