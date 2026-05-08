package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

import java.math.BigDecimal;

/**
 * Positive physical area measured in square feet.
 *
 * <p>The current property schema stores unit area in square feet. The value
 * object keeps that measurement positive without introducing unit conversion
 * logic.
 *
 * @param value the positive square-foot value
 */
public record Area(BigDecimal value) {

    /**
     * Creates an area value.
     *
     * @throws BusinessException if the area is missing, zero, or negative
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Area {
        if (value == null) {
            throw new BusinessException("Area is required");
        }
        if (value.signum() <= 0) {
            throw new BusinessException("Area must be positive");
        }
    }

    /**
     * Creates an optional area value.
     *
     * @param value the optional square-foot value
     * @return the area value, or {@code null} when absent
     */
    public static Area optional(BigDecimal value) {
        return value != null ? new Area(value) : null;
    }

    /**
     * Serializes this value as a plain decimal JSON value.
     *
     * @return the square-foot amount
     */
    @JsonValue
    public BigDecimal toJson() {
        return value;
    }
}
