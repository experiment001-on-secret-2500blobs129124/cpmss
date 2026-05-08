package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.BusinessException;

import java.time.Duration;
import java.time.Instant;

/**
 * Timestamp window with a required start and end instant.
 *
 * <p>Use this value object when ordering must be checked on absolute time,
 * such as gate guard assignments or other timestamp-based shifts.
 *
 * @param start the inclusive start instant
 * @param end the exclusive end instant
 */
public record InstantWindow(Instant start, Instant end) {

    /**
     * Creates an instant window.
     *
     * @throws BusinessException if either instant is missing or the end is not
     *                           after the start
     */
    public InstantWindow {
        if (start == null) {
            throw new BusinessException("Start instant is required");
        }
        if (end == null) {
            throw new BusinessException("End instant is required");
        }
        if (!end.isAfter(start)) {
            throw new BusinessException("End instant must be after start instant");
        }
    }

    /**
     * Calculates the duration of the instant window.
     *
     * @return the positive duration between start and end
     */
    public Duration duration() {
        return Duration.between(start, end);
    }
}
