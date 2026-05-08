package com.cpmss.identity.auth;

import com.cpmss.platform.exception.BusinessException;

/**
 * Business rules for authentication operations.
 *
 * <p>Stateless — no repositories injected. The service loads all
 * necessary data and passes it to these validation methods.
 *
 * @see AuthService
 */
public class AuthRules {

    /**
     * Validates that the system has no users yet (bootstrap precondition).
     *
     * @param userCount the current number of users in the system
     * @throws BusinessException if users already exist
     */
    public void validateSetupAllowed(long userCount) {
        if (userCount > 0) {
            throw new BusinessException("Setup is only allowed when no users exist");
        }
    }

    /**
     * Validates that the user account is active.
     *
     * @param active whether the account is active
     * @throws BusinessException if the account is deactivated
     */
    public void validateAccountActive(boolean active) {
        if (!active) {
            throw new BusinessException("Account is deactivated");
        }
    }
}
