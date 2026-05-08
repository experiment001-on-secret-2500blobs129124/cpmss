package com.cpmss.people.qualification;
import com.cpmss.platform.exception.ConflictException;
/**
 * Business rules for {@link Qualification} operations.
 *
 * @see QualificationService
 */
public class QualificationRules {
    /**
     * Validates that a qualification name is not already taken.
     *
     * @param name   the desired qualification name
     * @param exists whether a qualification with this name already exists
     * @throws ConflictException if the name is already in use
     */
    public void validateNameUnique(String name, boolean exists) {
        if (exists) {
            throw new ConflictException("Qualification '" + name + "' already exists");
        }
    }
}
