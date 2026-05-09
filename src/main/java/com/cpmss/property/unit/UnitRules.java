package com.cpmss.property.unit;

import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.common.PropertyErrorCode;

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
     * @throws ApiException if the unit number is already in use
     */
    public void validateUnitNoUniqueInBuilding(String unitNo, boolean exists) {
        if (exists) {
            throw new ApiException(PropertyErrorCode.UNIT_DUPLICATE);
        }
    }
}
