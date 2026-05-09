package com.cpmss.identity.auth;

import com.cpmss.identity.common.IdentityErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @throws ApiException if users already exist
     */
    public void validateSetupAllowed(long userCount) {
        if (userCount > 0) {
            throw new ApiException(IdentityErrorCode.SETUP_ALREADY_DONE);
        }
    }

    /**
     * Validates that the user account is active.
     *
     * @param active whether the account is active
     * @throws ApiException if the account is deactivated
     */
    public void validateAccountActive(boolean active) {
        if (!active) {
            throw new ApiException(IdentityErrorCode.ACCOUNT_DISABLED);
        }
    }
}
