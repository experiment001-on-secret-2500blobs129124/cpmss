package com.cpmss.workforce.common;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;

/**
 * Signed hour difference between actual and expected attendance.
 *
 * <p>Positive values represent overtime, negative values represent shortfall,
 * and zero means the actual hours matched the shift law.
 *
 * @param hours signed hour difference
 */
public record HourDelta(BigDecimal hours) {

    /**
     * Creates a signed hour delta.
     *
     * @throws BusinessException if the value is missing
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public HourDelta {
        if (hours == null) {
            throw new BusinessException("Hour delta is required");
        }
    }

    /**
     * Creates an optional hour delta.
     *
     * @param hours optional raw hour delta
     * @return the hour delta, or {@code null} when absent
     */
    public static HourDelta optional(BigDecimal hours) {
        return hours != null ? new HourDelta(hours) : null;
    }

    /**
     * Reports whether this value represents overtime.
     *
     * @return true when the delta is above zero
     */
    public boolean isOvertime() {
        return hours.signum() > 0;
    }

    /**
     * Reports whether this value represents a shortfall.
     *
     * @return true when the delta is below zero
     */
    public boolean isShortfall() {
        return hours.signum() < 0;
    }

    /**
     * Serializes this value as a plain decimal JSON value.
     *
     * @return the signed hour quantity
     */
    @JsonValue
    public BigDecimal toJson() {
        return hours;
    }
}
