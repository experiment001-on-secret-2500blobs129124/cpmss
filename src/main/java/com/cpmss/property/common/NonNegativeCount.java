package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.cpmss.platform.exception.BusinessException;

/**
 * Count value for property measurements that cannot be negative.
 *
 * <p>Used for room, balcony, and floor-count fields where the database stores
 * an integer but the domain should reject negative values.
 *
 * @param value the non-negative count
 */
public record NonNegativeCount(Integer value) {

    /**
     * Creates a non-negative count.
     *
     * @throws BusinessException if the count is missing or negative
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public NonNegativeCount {
        if (value == null) {
            throw new BusinessException("Count is required");
        }
        if (value < 0) {
            throw new BusinessException("Count cannot be negative");
        }
    }

    /**
     * Creates an optional count value.
     *
     * @param value the optional integer value
     * @return the count value, or {@code null} when absent
     */
    public static NonNegativeCount optional(Integer value) {
        return value != null ? new NonNegativeCount(value) : null;
    }

    /**
     * Serializes this value as a plain integer JSON value.
     *
     * @return the count integer
     */
    @JsonValue
    public Integer toJson() {
        return value;
    }
}
