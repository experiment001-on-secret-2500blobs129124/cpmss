package com.cpmss.people.person;

import com.cpmss.people.common.EgyptianNationalId;
import com.cpmss.people.common.Gender;
import com.cpmss.people.common.PassportNumber;
import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @throws ApiException if the list is empty
     */
    public void validateAtLeastOneRole(List<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new ApiException(PeopleErrorCode.PERSON_ROLE_REQUIRED);
        }
    }

    /**
     * Validates gender value against the allowed vocabulary.
     *
     * @param gender the gender string
     * @return the typed gender, or {@code null} when absent
     * @throws ApiException if invalid
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
     * @throws ApiException if an Egyptian person has no valid national ID
     */
    public EgyptianNationalId validateEgyptianNationalId(String nationality, String egyptianNationalId) {
        return EgyptianNationalId.forNationality(nationality, egyptianNationalId);
    }

    /**
     * Validates passport uniqueness.
     *
     * @param passportNo the passport number
     * @param exists     whether a person with this passport already exists
     * @throws ApiException if duplicate
     */
    public void validatePassportUnique(PassportNumber passportNo, boolean exists) {
        if (exists) {
            throw new ApiException(PeopleErrorCode.PASSPORT_DUPLICATE);
        }
    }
}
