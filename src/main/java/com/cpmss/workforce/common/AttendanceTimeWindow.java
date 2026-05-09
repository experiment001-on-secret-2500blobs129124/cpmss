package com.cpmss.workforce.common;

import com.cpmss.platform.exception.ApiException;
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
 * Optional same-day attendance check-in/check-out window.
 *
 * <p>Absence is represented by the owning attendance row, not by this value.
 * When the value is present, both times must be present and checkout must be
 * after check-in. Rules that decide whether the window is required stay in
 * {@code AttendsRules}.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceTimeWindow {

    /** Actual check-in time. */
    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    /** Actual check-out time. */
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    /**
     * Creates an attendance time window.
     *
     * @param checkInTime actual check-in time
     * @param checkOutTime actual check-out time
     * @throws ApiException if either time is missing or check-out is not
     *                           after check-in
     */
    @JsonCreator
    public AttendanceTimeWindow(@JsonProperty("checkInTime") LocalTime checkInTime,
                                @JsonProperty("checkOutTime") LocalTime checkOutTime) {
        if (checkInTime == null) {
            throw new ApiException(WorkforceErrorCode.CHECKIN_REQUIRED);
        }
        if (checkOutTime == null) {
            throw new ApiException(WorkforceErrorCode.CHECKOUT_REQUIRED);
        }
        if (!checkOutTime.isAfter(checkInTime)) {
            throw new ApiException(WorkforceErrorCode.CHECKOUT_BEFORE_CHECKIN);
        }
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    /**
     * Creates an optional attendance window from raw times.
     *
     * @param checkInTime optional check-in time
     * @param checkOutTime optional check-out time
     * @return the attendance window, or {@code null} when both times are absent
     * @throws ApiException if only one time is present or ordering is invalid
     */
    public static AttendanceTimeWindow optional(LocalTime checkInTime, LocalTime checkOutTime) {
        if (checkInTime == null && checkOutTime == null) {
            return null;
        }
        return new AttendanceTimeWindow(checkInTime, checkOutTime);
    }
}
