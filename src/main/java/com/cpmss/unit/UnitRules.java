package com.cpmss.unit;

import com.cpmss.exception.ConflictException;

/**
 * Business rules for {@link Unit} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class UnitRules {

    /**
     * Validates that a unit number is unique within a building.
     *
     * @param unitNo the desired unit number
     * @param exists whether a unit with this number exists in the building
     * @throws ConflictException if the unit number is already in use
     */
    public void validateUnitNoUniqueInBuilding(String unitNo, boolean exists) {
        if (exists) {
            throw new ConflictException(
                    "Unit number '" + unitNo + "' already exists in this building");
        }
    }
}
