package com.cpmss.property.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cpmss.platform.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Optional same-day operating-hours window for a facility.
 *
 * <p>Both times must be present together. Overnight hours need an explicit
 * workflow decision before this value is changed to support crossing midnight.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHours {

    /** Opening time for the facility. */
    @Column(name = "opening_time")
    private LocalTime openingTime;

    /** Closing time for the facility. */
    @Column(name = "closing_time")
    private LocalTime closingTime;

    /**
     * Creates an operating-hours window.
     *
     * @param openingTime the opening time
     * @param closingTime the closing time
     * @throws ApiException if only one time is present or the closing
     *                      time is not after the opening time
     */
    @JsonCreator
    public OperatingHours(@JsonProperty("openingTime") LocalTime openingTime,
                          @JsonProperty("closingTime") LocalTime closingTime) {
        if ((openingTime == null) != (closingTime == null)) {
            throw new ApiException(PropertyErrorCode.OPERATING_HOURS_INCOMPLETE);
        }
        if (openingTime != null && !closingTime.isAfter(openingTime)) {
            throw new ApiException(PropertyErrorCode.OPERATING_HOURS_INVALID);
        }
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    /**
     * Creates an optional operating-hours window.
     *
     * @param openingTime the optional opening time
     * @param closingTime the optional closing time
     * @return the operating hours, or {@code null} when both times are absent
     */
    public static OperatingHours optional(LocalTime openingTime, LocalTime closingTime) {
        if (openingTime == null && closingTime == null) {
            return null;
        }
        return new OperatingHours(openingTime, closingTime);
    }
}
