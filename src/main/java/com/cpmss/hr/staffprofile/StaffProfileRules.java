package com.cpmss.hr.staffprofile;

import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ConflictException;

import java.util.UUID;

/**
 * Business rules for {@link StaffProfile} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see StaffProfileService
 */
public class StaffProfileRules {

    /**
     * Validates that no staff profile already exists for the given person.
     *
     * @param personId the person's UUID
     * @param exists   whether a profile already exists
     * @throws ConflictException if a profile already exists for this person
     */
    public void validateProfileNotExists(UUID personId, boolean exists) {
        if (exists) {
            throw new ConflictException(
                    "Staff profile already exists for person " + personId);
        }
    }
}
