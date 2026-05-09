package com.cpmss.platform.common.value;

import com.cpmss.platform.exception.BusinessException;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Same-day local time window with a required start and end time.
 *
 * <p>This value object is intended for shift laws, attendance windows, and
 * facility operating hours that do not cross midnight. Overnight policies need
 * an explicit workflow decision before using this type.
 *
 * @param startTime the inclusive window start time
 * @param endTime the exclusive window end time
 */
public record LocalTimeWindow(LocalTime startTime, LocalTime endTime) {

    /**
     * Creates a local time window.
     *
     * @throws BusinessException if either time is missing or the end is not
     *                           after the start
     */
    public LocalTimeWindow {
        if (startTime == null) {
            throw new BusinessException("Start time is required");
        }
        if (endTime == null) {
            throw new BusinessException("End time is required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("End time must be after start time");
        }
    }

    /**
     * Calculates the duration of the time window.
     *
     * @return the positive duration between start and end
     */
    public Duration duration() {
        return Duration.between(startTime, endTime);
    }
}
