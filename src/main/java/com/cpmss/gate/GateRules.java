package com.cpmss.gate;

import com.cpmss.exception.ConflictException;

/**
 * Business rules for {@link Gate} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 */
public class GateRules {

    /**
     * Validates that a gate number is unique system-wide.
     *
     * @param gateNo the desired gate number
     * @param exists whether a gate with this number already exists
     * @throws ConflictException if the gate number is already in use
     */
    public void validateGateNoUnique(String gateNo, boolean exists) {
        if (exists) {
            throw new ConflictException("Gate number '" + gateNo + "' is already in use");
        }
    }
}
