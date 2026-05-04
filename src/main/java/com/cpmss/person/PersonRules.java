package com.cpmss.person;

import com.cpmss.exception.BusinessException;
import com.cpmss.exception.ConflictException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Business rules for {@link Person} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class PersonRules {

    private static final Set<String> VALID_GENDERS = Set.of("Male", "Female");

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
     * @throws BusinessException if invalid
     */
    public void validateGender(String gender) {
        if (gender != null && !VALID_GENDERS.contains(gender)) {
            throw new BusinessException("Gender must be one of: " + VALID_GENDERS);
        }
    }

    /**
     * Validates Egyptian national ID requirement for Egyptian nationals.
     *
     * @param nationality        the person's nationality
     * @param egyptianNationalId the national ID
     * @throws BusinessException if an Egyptian person has no national ID
     */
    public void validateEgyptianNationalId(String nationality, String egyptianNationalId) {
        if ("Egyptian".equalsIgnoreCase(nationality) && (egyptianNationalId == null || egyptianNationalId.isBlank())) {
            throw new BusinessException("Egyptian nationals must provide a national ID");
        }
    }

    /**
     * Validates passport uniqueness.
     *
     * @param passportNo the passport number
     * @param exists     whether a person with this passport already exists
     * @throws ConflictException if duplicate
     */
    public void validatePassportUnique(String passportNo, boolean exists) {
        if (exists) {
            throw new ConflictException("Passport number '" + passportNo + "' is already registered");
        }
    }
}
