package com.cpmss.security.gate;

import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.platform.exception.ApiException;

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
     * @throws ApiException if the gate number is already in use
     */
    public void validateGateNoUnique(String gateNo, boolean exists) {
        if (exists) {
            throw new ApiException(SecurityErrorCode.GATE_DUPLICATE);
        }
    }
}
