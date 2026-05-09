package com.cpmss.organization.personsupervision;

import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Business rules for {@link PersonSupervision} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see PersonSupervisionService
 */
public class PersonSupervisionRules {

    /**
     * Validates that a person does not supervise themselves.
     *
     * @param supervisorId the supervisor's UUID
     * @param superviseeId the supervisee's UUID
     * @throws ApiException if both IDs refer to the same person
     */
    public void validateNoSelfSupervision(UUID supervisorId, UUID superviseeId) {
        if (supervisorId.equals(superviseeId)) {
            throw new ApiException(OrganizationErrorCode.SELF_SUPERVISION_FORBIDDEN);
        }
    }
}
