package com.cpmss.workforce.common;

import com.cpmss.platform.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Required same-day time window for a shift attendance law.
 *
 * <p>Overnight shifts need a separate policy decision before this value is
 * widened to support crossing midnight.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShiftTimeWindow {

    /** Shift start time. */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** Shift end time. */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Creates a shift time window.
     *
     * @param startTime shift start time
     * @param endTime shift end time
     * @throws BusinessException if either time is missing or the end is not
     *                           after the start
     */
    @JsonCreator
    public ShiftTimeWindow(@JsonProperty("startTime") LocalTime startTime,
                           @JsonProperty("endTime") LocalTime endTime) {
        if (startTime == null) {
            throw new BusinessException("Shift start time is required");
        }
        if (endTime == null) {
            throw new BusinessException("Shift end time is required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("Shift end time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
