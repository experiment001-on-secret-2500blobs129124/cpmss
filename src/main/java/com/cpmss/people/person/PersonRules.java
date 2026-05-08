package com.cpmss.people.person;

import com.cpmss.people.common.EgyptianNationalId;
import com.cpmss.people.common.Gender;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.platform.exception.BusinessException;
import com.cpmss.platform.exception.ConflictException;

import java.util.List;
import java.util.UUID;

/**
 * Business rules for {@link Person} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class PersonRules {

    /**
     * Validates that at least one role ID is provided.
     *
     * @param roleIds the list of role UUIDs
     * @throws BusinessException if the list is empty
     */
    public void validateAtLeastOneRole(List<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException("A person must have at least one role");
        }
    }

    /**
     * Validates gender value against the allowed vocabulary.
     *
     * @param gender the gender string
     * @return the typed gender, or {@code null} when absent
     * @throws BusinessException if invalid
     */
    public Gender validateGender(String gender) {
        return Gender.fromNullableLabel(gender);
    }

    /**
     * Validates Egyptian national ID requirement for Egyptian nationals.
     *
     * @param nationality        the person's nationality
     * @param egyptianNationalId the national ID
     * @return the typed national ID, or {@code null} when not required
     * @throws BusinessException if an Egyptian person has no valid national ID
     */
    public EgyptianNationalId validateEgyptianNationalId(String nationality, String egyptianNationalId) {
        return EgyptianNationalId.forNationality(nationality, egyptianNationalId);
    }

    /**
     * Validates passport uniqueness.
     *
     * @param passportNo the passport number
     * @param exists     whether a person with this passport already exists
     * @throws ConflictException if duplicate
     */
    public void validatePassportUnique(PassportNumber passportNo, boolean exists) {
        if (exists) {
            throw new ConflictException("Passport number '" + passportNo.value() + "' is already registered");
        }
    }
}
